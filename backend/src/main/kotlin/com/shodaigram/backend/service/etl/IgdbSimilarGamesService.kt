package com.shodaigram.backend.service.etl

import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.domain.entity.GameSimilarity
import com.shodaigram.backend.domain.entity.SimilarityType
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.repository.GameSimilarityRepository
import com.shodaigram.backend.util.EtlConstants.DEFAULT_SIMILARITY_SCORE
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Processes IGDB's similar_games references after all games are loaded.
 * Converts IGDB ID mappings to internal game ID relationships.
 */
interface IgdbSimilarGamesService {
    /**
     * Convert IGDB similar_games mapping to game_similarities records
     *
     * @param similarGamesMapping Map of source IGDB ID -> list of similar IGDB IDs
     * @param job ETL job for logging
     * @return Count of inserted similarity records
     */
    fun processSimilarGames(
        similarGamesMapping: Map<Long, List<Long>>,
        job: EtlJob,
    ): Int
}

@Service
class IgdbSimilarGamesServiceImpl(
    private val gameRepository: GameRepository,
    private val gameSimilarityRepository: GameSimilarityRepository,
    etlJobLogRepository: EtlJobLogRepository,
) : IgdbSimilarGamesService, AbstractEtlService(etlJobLogRepository) {
    @Transactional
    override fun processSimilarGames(
        similarGamesMapping: Map<Long, List<Long>>,
        job: EtlJob,
    ): Int {
        if (similarGamesMapping.isEmpty()) {
            logInfo(job, "No IGDB similar_games references to process")
            return 0
        }

        logInfo(job, "Processing ${similarGamesMapping.size} IGDB similar_games references...")

        val allIgdbIds = (similarGamesMapping.keys + similarGamesMapping.values.flatten()).toList()
        val igdbIdToGame =
            gameRepository.findAllByIgdbIdIn(allIgdbIds)
                .associateBy { it.igdbId!! }

        var insertedCount = 0
        var skippedCount = 0

        similarGamesMapping.forEach { (sourceIgdbId, similarIgdbIds) ->
            val sourceGame = igdbIdToGame[sourceIgdbId]
            if (sourceGame == null) {
                logWarn(job, "Source game not found for IGDB ID: $sourceIgdbId")
                skippedCount += similarIgdbIds.size
                return@forEach
            }

            similarIgdbIds.forEach { similarIgdbId ->
                val similarGameId = igdbIdToGame[similarIgdbId]
                if (similarGameId == null) {
                    logger.debug("Similar game not found for IGDB ID: $similarIgdbId (source: $sourceIgdbId)")
                    skippedCount++
                    return@forEach
                }

                similarIgdbIds.forEach { similarIgdbId ->
                    val result = runCatching { processSimilarityPair(sourceGame, similarIgdbId, igdbIdToGame, job) }

                    result.onSuccess { wasInserted -> if (wasInserted) insertedCount++ else skippedCount++ }
                        .onFailure { e ->
                            logError(
                                job,
                                "Failed to save similarity: ${sourceGame.id} → $similarIgdbId: ${e.message}",
                                e as Exception,
                            )
                            skippedCount++
                        }
                }
            }
        }

        logInfo(job, "IGDB similar_games processing complete. Inserted: $insertedCount, Skipped: $skippedCount")
        return insertedCount
    }

    private fun processSimilarityPair(
        sourceGame: Game,
        similarIgdbId: Long,
        igdbIdToGame: Map<Long, Game>,
        job: EtlJob,
    ): Boolean {
        val similarGame = igdbIdToGame[similarIgdbId]
        if (similarGame == null) {
            logWarn(job, "Similar game not found for IGDB ID: $similarIgdbId")
            return false
        }

        if (sourceGame.id == similarGame.id) {
            logWarn(job, "Skipping self-reference: game ID ${sourceGame.id}")
            return false
        }

        val exists = gameSimilarityRepository.existsByGamePair(sourceGame.id!!, similarGame.id!!)
        if (exists) return false

        val similarity =
            GameSimilarity(
                game = sourceGame,
                similarGame = similarGame,
                similarityScore = DEFAULT_SIMILARITY_SCORE,
                similarityType = SimilarityType.API_PROVIDED,
            )

        gameSimilarityRepository.save(similarity)
        return true
    }
}

package com.shodaigram.backend.service.etl

import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.EtlJobLog
import com.shodaigram.backend.domain.entity.GameSimilarity
import com.shodaigram.backend.domain.entity.SimilarityType
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.repository.GameSimilarityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

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
    fun processSimilarGames(similarGamesMapping: Map<Long, List<Long>>, job: EtlJob): Int
}

@Service
class IgdbSimilarGamesServiceImpl(
    private val gameRepository: GameRepository,
    private val gameSimilarityRepository: GameSimilarityRepository,
    private val etlJobLogRepository: EtlJobLogRepository,
) : IgdbSimilarGamesService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun processSimilarGames(similarGamesMapping: Map<Long, List<Long>>, job: EtlJob): Int {
        if (similarGamesMapping.isEmpty()) {
            logInfo(job, "No IGDB similar_games references to process")
            return 0
        }

        logInfo(job, "Processing ${similarGamesMapping.size} IGDB similar_games references...")

        val allIgdbIds = similarGamesMapping.keys + similarGamesMapping.values.flatten()
        val igdbIdToGameId = gameRepository.findAllByIgdbIdIn(allIgdbIds.toList())
            .associateBy({ it.igdbId }, { it.id })

        var insertedCount = 0
        var skippedCount = 0

        similarGamesMapping.forEach { (sourceIgdbId, similarIgdbIds) ->
            val sourceGameId = igdbIdToGameId[sourceIgdbId]
            if (sourceGameId == null) {
                logWarn(job, "Source game not found for IGDB ID: $sourceIgdbId")
                skippedCount += similarIgdbIds.size
                return@forEach
            }

            similarIgdbIds.forEach { similarIgdbId ->
                val similarGameId = igdbIdToGameId[similarIgdbId]
                if (similarGameId == null) {
                    logger.debug("Similar game not found for IGDB ID: $similarIgdbId (source: $sourceIgdbId)")
                    skippedCount++
                    return@forEach
                }

                // Prevent self-references
                if (sourceGameId == similarGameId) {
                    skippedCount++
                    return@forEach
                }

                try {
                    val existing = gameSimilarityRepository.findByGameIdAndType(sourceGameId, "api_provided")
                        .any { it.similarGame.igdbId == similarGameId }

                    if (!existing) {

//                        gameSimilarityRepository.save(
//                            GameSimilarity(
//                                gameId = sourceGameId,
//                                similarGameId = similarGameId,
//                                similarityScore = BigDecimal("0.75"), // Fixed baseline for API-provided
//                                similarityType = SimilarityType.API_PROVIDED,
//                            )
//                        )
                        insertedCount++
                    } else {
                        skippedCount++
                    }
                } catch (e: Exception) {
                    logError(job, "Failed to save similarity: $sourceGameId → $similarGameId: ${e.message}")
                    skippedCount++
                }
            }
        }

        logInfo(job, "IGDB similar_games processing complete. Inserted: $insertedCount, Skipped: $skippedCount")
        return insertedCount
    }

    private fun logInfo(job: EtlJob, message: String) {
        logger.info(message)
        etlJobLogRepository.save(EtlJobLog(job = job, logLevel = EtlJobLog.LogLevel.INFO, message = message))
    }

    private fun logWarn(job: EtlJob, message: String) {
        logger.warn(message)
        etlJobLogRepository.save(EtlJobLog(job = job, logLevel = EtlJobLog.LogLevel.WARN, message = message))
    }

    private fun logError(job: EtlJob, message: String) {
        logger.error(message)
        etlJobLogRepository.save(EtlJobLog(job = job, logLevel = EtlJobLog.LogLevel.ERROR, message = message))
    }
}

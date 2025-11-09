package com.shodaigram.backend.service.etl

import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.util.EtlConstants.MAX_LEVENSHTEIN_DISTANCE
import com.shodaigram.backend.util.EtlConstants.MIN_NAME_LENGTH
import com.shodaigram.backend.util.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Detects and merges duplicate games from RAWG and IGDB sources
 * Uses Levenshtein distance for fuzzy name matching
 */
interface GameMergeService {
    /**
     * Merge rules: Keep IGDB as primary, preserve both IDs, average ratings
     *
     * @param job ETL job for logging
     * @return Count of merged games
     */
    fun detectAndMergeDuplicates(job: EtlJob): Int
}

@Service
class GameMergeServiceImpl(
    private val gameRepository: GameRepository,
    etlJobLogRepository: EtlJobLogRepository,
) : GameMergeService, AbstractEtlService(etlJobLogRepository) {
    @Transactional
    override fun detectAndMergeDuplicates(job: EtlJob): Int {
        logInfo(job, "Starting duplicate detection and merge process...")

        val rawgGames = gameRepository.findAllRawgGamesWithTags()
        val igdbGames = gameRepository.findAllIgdbGamesWithTags()

        logInfo(job, "Loaded ${rawgGames.size} RAWG games and ${igdbGames.size} IGDB games")

        // Optimization: Group IGDB games by release year to reduce comparisons
        val igdbGamesByYear = igdbGames.groupBy { it.releaseDate?.year }

        var mergedCount = 0
        val totalToProcess = rawgGames.size

        rawgGames.forEachIndexed { index, rawgGame ->
            // Progress logging every 100 games
            if (index > 0 && index % 100 == 0) {
                logInfo(
                    job,
                    "Duplicate detection progress: " +
                        "$index/$totalToProcess (${index * 100 / totalToProcess}%)" +
                        " - Merged so far: $mergedCount",
                )
            }

            // Only check IGDB games from the same year (or null years)
            val candidateIgdbGames =
                (igdbGamesByYear[rawgGame.releaseDate?.year] ?: emptyList()) +
                    (igdbGamesByYear[null] ?: emptyList())

            candidateIgdbGames.find { igdbGame ->
                areDuplicates(rawgGame, igdbGame)
            }?.let { igdbGame ->
                mergeGames(rawgGame, igdbGame, job)
                mergedCount++
            }
        }

        logInfo(job, "Merge complete. Merged $mergedCount duplicates")
        return mergedCount
    }

    private fun areDuplicates(
        rawgGame: Game,
        igdbGame: Game,
    ): Boolean {
        if (rawgGame.slug.equals(igdbGame.slug, ignoreCase = true)) {
            return true
        }

        val normalizedRawgName = StringUtils.normalize(rawgGame.name)
        val normalizedIgdbName = StringUtils.normalize(igdbGame.name)

        if (normalizedRawgName.length < MIN_NAME_LENGTH || normalizedIgdbName.length < MIN_NAME_LENGTH) {
            return false
        }

        val distance = StringUtils.levenshteinDistance(normalizedRawgName, normalizedIgdbName)
        val sameReleaseYear = rawgGame.releaseDate?.year == igdbGame.releaseDate?.year

        return distance <= MAX_LEVENSHTEIN_DISTANCE && sameReleaseYear
    }

    /**
     * Merge RAWG game into IGDB game using pure JDBC.
     * No Hibernate entities involved at all.
     */
    private fun mergeGames(
        rawgGame: Game,
        igdbGame: Game,
        job: EtlJob,
    ) {
        logInfo(job, "Merging: '${rawgGame.name}' (RAWG) → '${igdbGame.name}' (IGDB)")

        // Just get the IDs - nothing else
        val rawgGameId = rawgGame.id!!
        val igdbGameId = igdbGame.id!!

        // Use the repository method that does everything in pure SQL
        gameRepository.mergeGameData(rawgGameId, igdbGameId)

        logInfo(job, "Merge complete")
    }
}

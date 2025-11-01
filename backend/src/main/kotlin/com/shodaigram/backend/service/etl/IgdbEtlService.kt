package com.shodaigram.backend.service.etl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.shodaigram.backend.domain.dto.etl.IgdbGameDto
import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.EtlJobLog
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File

/**
 * ETL service for processing IGDB JSON files.
 * Handles IGDB similar_games extraction and storage
 */
interface IgdbEtlService {
    /**
     * Import games from IGDB JSON file
     *
     * @param filePath Absolute path to JSON file
     * @param job ETL job entity for tracking
     * @return (inserted count, skipped count, similar_games mapping
     */
    fun importIgdbGames(filePath: String, job: EtlJob): Triple<Int, Int, Map<Long, List<Long>>>
}

@Service
class IgdbEtlServiceImpl(
    private val gameRepository: GameRepository,
    private val etlJobLogRepository: EtlJobLogRepository,
    private val objectMapper: ObjectMapper
) : IgdbEtlService {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val BATCH_SIZE = 1000
        private const val LOG_INTERVAL = 500
    }

    @Transactional
    override fun importIgdbGames(
        filePath: String,
        job: EtlJob
    ): Triple<Int, Int, Map<Long, List<Long>>> {
        val file = File(filePath)
        if (!file.exists()) {
            logError(job, "IGDB JSON file not found: $filePath")
            throw IllegalArgumentException("File not found: $filePath")
        }

        logInfo(job, "Starting IGDB import from: $filePath")

        val igdbGames = try {
            objectMapper.readValue<List<IgdbGameDto>>(file)
        } catch (e: Exception) {
            logError(job, "Failed to parse IGDB JSON: ${e.message}")
            throw e
        }

        logInfo(job, "Parsed ${igdbGames.size} IGDB games from JSON")

        var insertedCount = 0
        var skippedCount = 0
        val similarGamesMapping = mutableMapOf<Long, List<Long>>() // igdbId → list of similar IGDB IDs

        igdbGames.chunked(BATCH_SIZE).forEachIndexed { chunkIndex, chunk ->
            val chunkResults = processIgdbChunk(chunk, job, similarGamesMapping)
            insertedCount += chunkResults.first
            skippedCount += chunkResults.second

            if ((chunkIndex + 1) * BATCH_SIZE % LOG_INTERVAL == 0 || chunkIndex == igdbGames.size / BATCH_SIZE) {
                val progress = ((chunkIndex + 1) * BATCH_SIZE).coerceAtMost(igdbGames.size)
                val percentage = (progress * 100.0 / igdbGames.size).toInt()
                logInfo(
                    job,
                    "IGDB Progress: $progress/${igdbGames.size} ($percentage%) - Inserted: $insertedCount, Skipped: $skippedCount"
                )
            }
        }

        logInfo(
            job,
            "IGDB import complete. Inserted: $insertedCount, Skipped: $skippedCount, Similar games references: ${similarGamesMapping.size}"
        )
        return Triple(insertedCount, skippedCount, similarGamesMapping)

    }

    private fun processIgdbChunk(
        chunk: List<IgdbGameDto>,
        job: EtlJob,
        similarGamesMapping: MutableMap<Long, List<Long>>,
    ): Pair<Int, Int> {
        var inserted = 0
        var skipped = 0

        chunk.forEach { igdbGame ->
            try {
                val existingGame = gameRepository.findByIgdbId(igdbGame.igdbId)
                    ?: gameRepository.findBySlugIgnoreCase(igdbGame.slug)

                if (existingGame != null) {
                    logWarn(
                        job,
                        "Duplicate IGDB game detected: ${igdbGame.name} (IGDB ID: ${igdbGame.igdbId}, existing ID: ${existingGame.id})"
                    )
                    skipped++
                } else {
                    val game = igdbGame.toEntity()
                    gameRepository.save(game)
                    inserted++

                    if (igdbGame.similarGames.isNotEmpty()) {
                        val similarIgdbIds = igdbGame.similarGames
                            .mapNotNull { it.toLongOrNull() }
                            .take(10)

                        if (similarIgdbIds.isNotEmpty()) {
                            similarGamesMapping[igdbGame.igdbId] = similarIgdbIds
                        }
                    }
                }
            } catch (e: Exception) {
                logError(job, "Failed to process IGDB game '${igdbGame.name}': ${e.message}")
                skipped++
            }
        }

        return Pair(inserted, skipped)
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

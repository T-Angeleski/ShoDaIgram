package com.shodaigram.backend.service.etl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.shodaigram.backend.domain.dto.etl.RawgGameDto
import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.EtlJobLog
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File

/**
 * ETL service for processing RAWG JSON files.
 */
interface RawgEtlService {
    /**
     * Import games from RAWG JSON file
     *
     * @param filePath Absolute path to JSON file
     * @param job ETL job entity for tracking
     * @return Pair of (inserted count, skipped count)
     */
    fun importRawgGames(filePath: String, job: EtlJob): Pair<Int, Int>
}

@Service
class RawgEtlServiceImpl(
    private val gameRepository: GameRepository,
    private val etlJobLogRepository: EtlJobLogRepository,
    private val objectMapper: ObjectMapper
) : RawgEtlService {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val BATCH_SIZE = 1000
        private const val LOG_INTERVAL = 500
    }

    @Transactional
    override fun importRawgGames(
        filePath: String,
        job: EtlJob
    ): Pair<Int, Int> {
        val file = File(filePath)
        if (!file.exists()) {
            logError(job, "RAWG JSON file not found: $filePath")
            throw IllegalArgumentException("File not found: $filePath")
        }

        logInfo(job, "Starting RAWG import from: $filePath")

        val rawgGames = try {
            objectMapper.readValue<List<RawgGameDto>>(file)
        } catch (e: Exception) {
            logError(job, "Failed to parse RAWG JSON: ${e.message}")
            throw e
        }

        logInfo(job, "Parsed ${rawgGames.size} RAWG games from JSON")

        var insertedCount = 0
        var skippedCount = 0

        rawgGames.chunked(BATCH_SIZE).forEachIndexed { chunkIndex, chunk ->
            val chunkResults = processRawgChunk(chunk, job)
            insertedCount += chunkResults.first
            skippedCount += chunkResults.second

            if ((chunkIndex + 1) * BATCH_SIZE % LOG_INTERVAL == 0 || chunkIndex == rawgGames.size / BATCH_SIZE) {
                val progress = ((chunkIndex + 1) * BATCH_SIZE).coerceAtMost(rawgGames.size)
                val percentage = (progress * 100.0 / rawgGames.size).toInt()

                logInfo(
                    job,
                    "RAWG Progress: $progress/${rawgGames.size} ($percentage%) - Inserted: $insertedCount, Skipped: $skippedCount"
                )
            }
        }

        logInfo(job, "RAWG import complete. Inserted: $insertedCount, Skipped: $skippedCount")
        return Pair(insertedCount, skippedCount)
    }

    private fun processRawgChunk(chunk: List<RawgGameDto>, job: EtlJob): Pair<Int, Int> {
        var inserted = 0
        var skipped = 0

        chunk.forEach { rawgGame ->
            try {
                val existingGame = gameRepository.findByRawgId(rawgGame.rawgId)
                    ?: gameRepository.findBySlugIgnoreCase(rawgGame.slug)

                if (existingGame != null) {
                    logWarn(
                        job,
                        "Duplicate RAWG game detected: ${rawgGame.name} (RAWG ID: ${rawgGame.rawgId}, existing ID: ${existingGame.id})"
                    )
                    skipped++
                } else {
                    val game = rawgGame.toEntity()
                    gameRepository.save(game)
                    inserted++
                }
            } catch (e: Exception) {
                logError(job, "Failed to process RAWG game '${rawgGame.name}': ${e.message}")
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

package com.shodaigram.backend.service.etl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import com.fasterxml.jackson.module.kotlin.readValue
import com.shodaigram.backend.domain.dto.etl.RawgGameDto
import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
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
    fun importRawgGames(
        filePath: String,
        job: EtlJob,
    ): Pair<Int, Int>
}

@Service
class RawgEtlServiceImpl(
    private val gameRepository: GameRepository,
    private val objectMapper: ObjectMapper,
    etlJobLogRepository: EtlJobLogRepository,
) : RawgEtlService, AbstractEtlService(etlJobLogRepository) {
    @Transactional
    override fun importRawgGames(
        filePath: String,
        job: EtlJob,
    ): Pair<Int, Int> {
        val file = File(filePath)
        require(file.exists()) { "RAWG JSON file not found: $filePath" }

        logInfo(job, "Starting RAWG import from: $filePath")

        val rawgGames =
            try {
                objectMapper.readValue<List<RawgGameDto>>(file)
            } catch (e: RuntimeJsonMappingException) {
                logError(job, "Failed to parse RAWG JSON: ${e.message}")
                throw e
            }

        logInfo(job, "Parsed ${rawgGames.size} RAWG games from JSON")

        val (insertedCount, skippedCount) =
            processBatched(rawgGames, job) { chunk ->
                processRawgChunk(chunk, job)
            }

        logInfo(job, "RAWG import complete. Inserted: $insertedCount, Skipped: $skippedCount")
        return Pair(insertedCount, skippedCount)
    }

    private fun processRawgChunk(
        chunk: List<RawgGameDto>,
        job: EtlJob,
    ): Pair<Int, Int> {
        var inserted = 0
        var skipped = 0

        chunk.forEach { rawgGame ->
            val result = runCatching { processRawgGame(rawgGame, job) }

            result.onSuccess { wasInserted -> if (wasInserted) inserted++ else skipped++ }
                .onFailure { e ->
                    logError(
                        job,
                        "Failed to process RAWG game '${rawgGame.name}': ${e.message}",
                        e as Exception,
                    )
                    skipped++
                }
        }

        return Pair(inserted, skipped)
    }

    private fun processRawgGame(
        rawgGame: RawgGameDto,
        job: EtlJob,
    ): Boolean {
        val existingGame =
            gameRepository.findByRawgId(rawgGame.rawgId)
                ?: gameRepository.findBySlugIgnoreCase(rawgGame.slug)

        return if (existingGame != null) {
            logWarn(
                job,
                "Duplicate RAWG game detected: ${rawgGame.name} (RAWG ID: ${rawgGame.rawgId}, " +
                    "existing ID: ${existingGame.id})",
            )
            false
        } else {
            val newGame = rawgGame.toEntity()
            gameRepository.save(newGame)
            true
        }
    }
}

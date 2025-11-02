package com.shodaigram.backend.service.etl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import com.fasterxml.jackson.module.kotlin.readValue
import com.shodaigram.backend.domain.dto.etl.IgdbGameDto
import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.util.EtlConstants.MAX_SIMILAR_GAMES
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
    fun importIgdbGames(
        filePath: String,
        job: EtlJob,
    ): Triple<Int, Int, Map<Long, List<Long>>>
}

@Service
class IgdbEtlServiceImpl(
    private val gameRepository: GameRepository,
    private val objectMapper: ObjectMapper,
    etlJobLogRepository: EtlJobLogRepository,
) : IgdbEtlService, AbstractEtlService(etlJobLogRepository) {
    @Transactional
    override fun importIgdbGames(
        filePath: String,
        job: EtlJob,
    ): Triple<Int, Int, Map<Long, List<Long>>> {
        val file = File(filePath)
        require(file.exists()) { "IGDB JSON file not found: $filePath" }

        logInfo(job, "Starting IGDB import from: $filePath")

        val igdbGames =
            try {
                objectMapper.readValue<List<IgdbGameDto>>(file)
            } catch (e: RuntimeJsonMappingException) {
                logError(job, "Failed to parse IGDB JSON: ${e.message}")
                throw e
            }

        logInfo(job, "Parsed ${igdbGames.size} IGDB games from JSON")

        val similarGamesMapping = mutableMapOf<Long, List<Long>>() // igdbId → list of similar IGDB IDs

        val (insertedCount, skippedCount) =
            processBatched(igdbGames, job) { chunk ->
                processIgdbChunk(chunk, job, similarGamesMapping)
            }

        logInfo(
            job,
            "IGDB import complete. Inserted: $insertedCount, Skipped: $skippedCount, " +
                "Similar games references: ${similarGamesMapping.size}",
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
            val result = runCatching { processIgdbGame(igdbGame, job, similarGamesMapping) }

            result.onSuccess { wasInserted -> if (wasInserted) inserted++ else skipped++ }
                .onFailure { e ->
                    logError(job, "Failed to process IGDB game '${igdbGame.name}': ${e.message}", e as Exception)
                    skipped++
                }
        }

        return Pair(inserted, skipped)
    }

    private fun processIgdbGame(
        igdbGame: IgdbGameDto,
        job: EtlJob,
        similarGamesMapping: MutableMap<Long, List<Long>>,
    ): Boolean {
        val existingGame =
            gameRepository.findByIgdbId(igdbGame.igdbId)
                ?: gameRepository.findBySlugIgnoreCase(igdbGame.slug)

        val isDuplicate = existingGame != null
        return if (isDuplicate) {
            logWarn(
                job,
                "Duplicate IGDB game detected: ${igdbGame.name} (IGDB ID: ${igdbGame.igdbId}, " +
                    "existing ID: ${existingGame.id})",
            )
            false
        } else {
            val newGame = igdbGame.toEntity()
            gameRepository.save(newGame)
            extractSimilarGames(igdbGame, similarGamesMapping)
            true
        }
    }

    private fun extractSimilarGames(
        igdbGame: IgdbGameDto,
        similarGamesMapping: MutableMap<Long, List<Long>>,
    ) {
        if (igdbGame.similarGames.isEmpty()) return

        val similarIgdbIds =
            igdbGame.similarGames
                .mapNotNull { it.toLongOrNull() }
                .take(MAX_SIMILAR_GAMES)

        if (similarIgdbIds.isNotEmpty()) {
            similarGamesMapping[igdbGame.igdbId] = similarIgdbIds
        }
    }
}

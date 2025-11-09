package com.shodaigram.backend.service.etl

import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import com.shodaigram.backend.domain.dto.etl.EtlReport
import com.shodaigram.backend.domain.entity.DataSource
import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.JobStatus
import com.shodaigram.backend.exception.EtlException
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.repository.EtlJobRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Orchestrates the full ETL pipeline:
 * 1. Import RAWG games
 * 2. Import IGDB games + similar_games references
 * 3. Process IGDB similar_games
 * 4. Merge duplicates (including tags)
 */
interface EtlOrchestratorService {
    /**
     * Run complete ETL pipeline.
     *
     * @param rawgFilePath Path to RAWG JSON file
     * @param igdbFilePath Path to IGDB JSON file
     * @return ETL report
     */
    fun runEtl(
        rawgFilePath: String,
        igdbFilePath: String,
    ): EtlReport
}

@Service
class EtlOrchestratorServiceImpl(
    private val etlJobRepository: EtlJobRepository,
    private val rawgEtlService: RawgEtlService,
    private val igdbEtlService: IgdbEtlService,
    private val igdbSimilarGamesService: IgdbSimilarGamesService,
    private val gameMergeService: GameMergeService,
    etlJobLogRepository: EtlJobLogRepository,
) : EtlOrchestratorService, AbstractEtlService(etlJobLogRepository) {
    @Transactional
    override fun runEtl(
        rawgFilePath: String,
        igdbFilePath: String,
    ): EtlReport {
        val startTime = LocalDateTime.now()

        val rawgJob = createJob(DataSource.RAWG)
        logInfo(rawgJob, "========== ETL PIPELINE START ==========")

        logInfo(rawgJob, "Phase 1: Importing RAWG games...")
        val (rawgInserted, rawgSkipped) =
            executePhase(rawgJob) {
                rawgEtlService.importRawgGames(rawgFilePath, rawgJob)
            }

        val igdbJob = createJob(DataSource.IGDB)
        logInfo(igdbJob, "Phase 2: Importing IGDB games...")

        val igdbResult =
            executePhase(igdbJob) {
                igdbEtlService.importIgdbGames(igdbFilePath, igdbJob)
            }

        logInfo(igdbJob, "Phase 3: Processing IGDB similar_games references...")
        val similaritiesInserted =
            executeSafePhase(igdbJob, "Similar games processing") {
                igdbSimilarGamesService.processSimilarGames(igdbResult.similarGamesMapping, igdbJob)
            }

        logInfo(igdbJob, "Phase 4: Detecting and merging duplicates...")
        val gamesMerged =
            executeSafePhase(igdbJob, "Game merge") {
                gameMergeService.detectAndMergeDuplicates(igdbJob)
            }

        val endTime = LocalDateTime.now()
        val durationMs = java.time.Duration.between(startTime, endTime).toMillis()

        logInfo(igdbJob, "========== ETL PIPELINE COMPLETE ($durationMs ms) ==========")

        return EtlReport(
            rawgJob = rawgJob,
            igdbJob = igdbJob,
            rawgInserted = rawgInserted,
            rawgSkipped = rawgSkipped,
            igdbInserted = igdbResult.insertedCount,
            igdbSkipped = igdbResult.skippedCount,
            similaritiesInserted = similaritiesInserted,
            gamesMerged = gamesMerged,
            tagsNormalized = 0,
            durationMs = durationMs,
        )
    }

    /**
     * Execute a phase that must succeed (import)
     * Marks job as completed on success, failed on error.
     */
    private fun <T> executePhase(
        job: EtlJob,
        block: () -> T,
    ): T {
        return try {
            val result = block()
            job.markCompleted()
            etlJobRepository.save(job)
            result
        } catch (e: RuntimeJsonMappingException) {
            job.markFailed(e.message ?: "Unknown error")
            etlJobRepository.save(job)
            throw EtlException("Phase failed for ${job.source}", e)
        }
    }

    /**
     * Execute a phase that can fail gracefully (merge, normalize).
     * Returns 0 on error instead of throwing.
     */
    private fun executeSafePhase(
        job: EtlJob,
        phaseName: String,
        block: () -> Int,
    ): Int {
        return try {
            block()
        } catch (e: EtlException) {
            logError(job, "$phaseName failed: ${e.message}", e)
            0
        } catch (e: IllegalArgumentException) {
            logError(job, "$phaseName failed: ${e.message}", e)
            0
        }
    }

    private fun createJob(source: DataSource): EtlJob {
        val job =
            EtlJob(
                source = source,
                status = JobStatus.PENDING,
            )
        return etlJobRepository.save(job)
    }
}

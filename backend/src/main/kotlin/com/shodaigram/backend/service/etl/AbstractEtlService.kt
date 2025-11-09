package com.shodaigram.backend.service.etl

import com.shodaigram.backend.domain.entity.EtlJob
import com.shodaigram.backend.domain.entity.EtlJobLog
import com.shodaigram.backend.repository.EtlJobLogRepository
import com.shodaigram.backend.util.EtlConstants.BATCH_SIZE
import com.shodaigram.backend.util.EtlConstants.LOG_INTERVAL
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base class for all ETL services with standardized logging.
 */
abstract class AbstractEtlService(
    private val etlJobLogRepository: EtlJobLogRepository,
) {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Process items in batches with progress logging
     *
     * @param items Full list of items to process
     * @param job ETL job for tracking
     * @param batchSize Number of items per batch
     * @param logInterval Log progress every N items
     * @param processor Function to process a single batch, returns (inserted, skipped)
     * @return Pair of (total inserted, total skipped)
     */
    protected fun <T> processBatched(
        items: List<T>,
        job: EtlJob,
        batchSize: Int = BATCH_SIZE,
        logInterval: Int = LOG_INTERVAL,
        processor: (List<T>) -> Pair<Int, Int>,
    ): Pair<Int, Int> {
        var totalInserted = 0
        var totalSkipped = 0

        items.chunked(batchSize).forEachIndexed { chunkIndex, chunk ->
            val (inserted, skipped) = processor(chunk)
            totalInserted += inserted
            totalSkipped += skipped

            val processedCount = (chunkIndex + 1) * batchSize
            val shouldLog =
                processedCount % logInterval == 0 ||
                    chunkIndex == items.size / batchSize

            if (shouldLog) {
                val progress = processedCount.coerceAtMost(items.size)
                logProgress(job, progress, items.size, totalInserted, totalSkipped)
            }
        }

        return Pair(totalInserted, totalSkipped)
    }

    protected fun logInfo(
        job: EtlJob,
        message: String,
    ) {
        logger.info(message)
        etlJobLogRepository.save(
            EtlJobLog(
                job = job,
                logLevel = EtlJobLog.LogLevel.INFO,
                message = message,
            ),
        )
    }

    protected fun logWarn(
        job: EtlJob,
        message: String,
    ) {
        logger.warn(message)
        etlJobLogRepository.save(
            EtlJobLog(
                job = job,
                logLevel = EtlJobLog.LogLevel.WARN,
                message = message,
            ),
        )
    }

    protected fun logError(
        job: EtlJob,
        message: String,
        exception: Exception? = null,
    ) {
        if (exception != null) {
            logger.error(message, exception)
        } else {
            logger.error(message)
        }
        etlJobLogRepository.save(
            EtlJobLog(
                job = job,
                logLevel = EtlJobLog.LogLevel.ERROR,
                message = message,
            ),
        )
    }

    /**
     * Log progress at specified intervals.
     */
    protected fun logProgress(
        job: EtlJob,
        current: Int,
        total: Int,
        inserted: Int,
        skipped: Int,
    ) {
        val percentage = (current * 100.0 / total).toInt()
        logInfo(
            job,
            "Progress: $current/$total ($percentage%) - Inserted: $inserted, Skipped: $skipped",
        )
    }
}

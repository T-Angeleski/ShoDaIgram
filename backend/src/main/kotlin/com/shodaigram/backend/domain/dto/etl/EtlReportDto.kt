package com.shodaigram.backend.domain.dto.etl

import com.shodaigram.backend.domain.entity.EtlJob

data class EtlReport(
    val rawgJob: EtlJob,
    val igdbJob: EtlJob,
    val rawgInserted: Int,
    val rawgSkipped: Int,
    val igdbInserted: Int,
    val igdbSkipped: Int,
    val similaritiesInserted: Int,
    val gamesMerged: Int,
    val tagsNormalized: Int,
    val durationMs: Long,
) {
    fun toDto() =
        EtlReportDto(
            rawgJobId = rawgJob.id!!,
            igdbJobId = igdbJob.id!!,
            summary =
                EtlSummaryDto(
                    rawgInserted = rawgInserted,
                    rawgSkipped = rawgSkipped,
                    igdbInserted = igdbInserted,
                    igdbSkipped = igdbSkipped,
                    similaritiesInserted = similaritiesInserted,
                    gamesMerged = gamesMerged,
                    tagsNormalized = tagsNormalized,
                ),
            durationMs = durationMs,
        )
}

data class EtlReportDto(
    val rawgJobId: Long,
    val igdbJobId: Long,
    val summary: EtlSummaryDto,
    val durationMs: Long,
)

data class EtlSummaryDto(
    val rawgInserted: Int,
    val rawgSkipped: Int,
    val igdbInserted: Int,
    val igdbSkipped: Int,
    val similaritiesInserted: Int,
    val gamesMerged: Int,
    val tagsNormalized: Int,
)

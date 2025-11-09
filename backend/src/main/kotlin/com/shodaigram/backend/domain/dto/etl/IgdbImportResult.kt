package com.shodaigram.backend.domain.dto.etl

/**
 * Result of IGDB games import process.
 * Contains insertion stats and mapping of similar games relationships for later processing.
 */
data class IgdbImportResult(
    val insertedCount: Int,
    val skippedCount: Int,
    val similarGamesMapping: Map<Long, List<Long>>,
)

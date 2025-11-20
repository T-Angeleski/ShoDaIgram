package com.shodaigram.backend.domain.dto.game

/**
 * Response for filtered game results with applied filter metadata.
 */
data class GameFilterResponse(
    val games: List<GameSummaryDto>,
    val appliedFilters: AppliedFilters,
    val page: Int,
    val pageSize: Int,
    val totalResults: Long,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)

/**
 * Metadata showing which filters were applied to the query.
 */
data class AppliedFilters(
    val tags: List<String>?,
    val minRating: Double?,
    val maxRating: Double?,
    val minYear: Int?,
    val maxYear: Int?,
    val platforms: List<String>?,
)

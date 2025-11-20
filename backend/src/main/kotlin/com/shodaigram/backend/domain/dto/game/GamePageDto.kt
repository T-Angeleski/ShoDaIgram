package com.shodaigram.backend.domain.dto.game

/**
 * Paginated response for game listings.
 */
data class GamePageDto(
    val games: List<GameSummaryDto>,
    val page: Int,
    val pageSize: Int,
    val totalResults: Long,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)

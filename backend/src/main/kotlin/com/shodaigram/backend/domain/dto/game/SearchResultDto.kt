package com.shodaigram.backend.domain.dto.game

/**
 * Response wrapper for game search results.
 * Includes search metadata for analytics and debugging.
 */
data class SearchResultDto(
    val games: List<GameSearchDto>,
    val query: String,
    val totalResults: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)

/**
 * Game search result with BM25 relevance ranking.
 */
data class GameSearchDto(
    val id: Long,
    val name: String,
    val slug: String,
    val description: String?,
    val rating: java.math.BigDecimal?,
    val ratingCount: Int,
    val releaseDate: String?,
    val backgroundImageUrl: String?,
    val searchRank: Double,
)

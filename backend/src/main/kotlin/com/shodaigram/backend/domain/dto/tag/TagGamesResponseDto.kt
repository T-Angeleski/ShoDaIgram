package com.shodaigram.backend.domain.dto.tag

import com.shodaigram.backend.domain.dto.game.GameSummaryDto
import com.shodaigram.backend.domain.entity.TagCategory

/**
 * Response for games associated with a specific tag
 */
data class TagGamesResponseDto(
    val tag: TagInfo,
    val games: List<GameSummaryDto>,
    val page: Int,
    val pageSize: Int,
    val totalResults: Long,
    val totalPages: Int,
)

/**
 * Metadata about the tag being queried
 */
data class TagInfo(
    val name: String,
    val normalizedName: String,
    val category: TagCategory,
)

package com.shodaigram.backend.domain.dto.tag

/**
 * Paginated response for tag browsing and search.
 */
data class TagListResponse(
    val tags: List<TagSummaryDto>,
    val totalResults: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)

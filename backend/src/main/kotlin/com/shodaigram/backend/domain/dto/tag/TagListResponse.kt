package com.shodaigram.backend.domain.dto.tag

/**
 * Paginated response for tag browsing and search.
 */
data class TagListResponse(
    val tags: List<TagSummaryDto>,
    val totalTags: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
)

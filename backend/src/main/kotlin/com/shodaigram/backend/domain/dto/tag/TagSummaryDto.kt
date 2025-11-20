package com.shodaigram.backend.domain.dto.tag

import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.domain.entity.TagCategory

/**
 * DTO representing a tag with game count.
 */
data class TagSummaryDto(
    val id: Long,
    val name: String,
    val normalizedName: String,
    val category: TagCategory,
    val gameCount: Long,
) {
    companion object {
        fun fromEntity(
            tag: Tag,
            gameCount: Long,
        ) = TagSummaryDto(
            id = tag.id!!,
            name = tag.name,
            normalizedName = tag.normalizedName,
            category = tag.category,
            gameCount = gameCount,
        )
    }
}

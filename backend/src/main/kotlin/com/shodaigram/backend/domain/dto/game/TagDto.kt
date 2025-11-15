package com.shodaigram.backend.domain.dto.game

import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.domain.entity.TagCategory

data class TagDto(
    val id: Long,
    val name: String,
    val category: TagCategory,
) {
    companion object {
        fun fromEntity(tag: Tag) =
            TagDto(
                id = tag.id!!,
                name = tag.name,
                category = tag.category,
            )
    }
}

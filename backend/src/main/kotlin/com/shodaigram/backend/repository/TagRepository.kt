package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.domain.entity.TagCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<Tag, Long> {
    /**
     * Find tag by normalized name and category.
     * Used for deduplication during tag extraction.
     */
    fun findByNormalizedNameAndCategory(
        normalizedName: String,
        category: TagCategory,
    ): Tag?
}

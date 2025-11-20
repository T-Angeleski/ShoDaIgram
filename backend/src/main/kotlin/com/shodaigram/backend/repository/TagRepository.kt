package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.domain.entity.TagCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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

    /**
     * Find tag by normalized name (case-insensitive).
     */
    fun findByNormalizedName(normalizedName: String): Tag?

    /**
     * Find all tags by category.
     */
    fun findByCategory(category: TagCategory): List<Tag>

    /**
     * Search tags by name containing the search term (case-insensitive).
     */
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    fun searchByName(searchTerm: String): List<Tag>

    /**
     * Get tag counts grouped by tag.
     * Returns pairs of (Tag, count).
     */
    @Query(
        """
        SELECT t, COUNT(gt.id)
        FROM Tag t
        LEFT JOIN t.gameTags gt
        GROUP BY t.id
        ORDER BY COUNT(gt.id) DESC
        """,
    )
    fun findAllWithGameCounts(): List<Array<Any>>

    /**
     * Get tag counts for a specific category.
     */
    @Query(
        """
        SELECT t, COUNT(gt.id)
        FROM Tag t
        LEFT JOIN t.gameTags gt
        WHERE t.category = :category
        GROUP BY t.id
        ORDER BY COUNT(gt.id) DESC
        """,
    )
    fun findByCategoryWithGameCounts(category: TagCategory): List<Array<Any>>

    /**
     * Search tags with game counts.
     */
    @Query(
        """
        SELECT t, COUNT(gt.id)
        FROM Tag t
        LEFT JOIN t.gameTags gt
        WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        GROUP BY t.id
        ORDER BY COUNT(gt.id) DESC
        """,
    )
    fun searchByNameWithGameCounts(searchTerm: String): List<Array<Any>>

    /**
     * Get game count for a specific tag.
     */
    @Query(
        """
        SELECT COUNT(gt.id)
        FROM GameTag gt
        WHERE gt.tag.id = :tagId
        """,
    )
    fun countGamesByTagId(tagId: Long): Long
}

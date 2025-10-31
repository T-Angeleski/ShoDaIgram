package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE LOWER(g.slug) = LOWER(:slug)")
    fun findBySlugIgnoreCase(slug: String): Game?

    fun findByIgdbId(igdbId: Long): Game?

    fun findByRawgId(rawgId: Long): Game?

    /**
     * Find games with similar names and matching release year
     * Uses PostgreSQL SIMILARITY function
     */
    @Query(
        """
        SELECT * FROM games g
        WHERE EXTRACT(YEAR FROM g.release_date) = :releaseYear
        AND SIMILARITY(LOWER(g.name), LOWER(:name)) > 0.6
        ORDER BY SIMILARITY(LOWER(g.name), LOWER(:name)) DESC
        """,
        nativeQuery = true,
    )
    fun findSimilarByNameAndYear(
        name: String,
        releaseYear: Int,
    ): List<Game>

    /**
     * Check if a game already exists by any identifier (IGDB/RAWG ID or slug)
     */
    @Query(
        """
        SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END
        FROM Game g
        WHERE g.igdbId = :igdbId OR g.rawgId = :rawgId OR LOWER(g.slug) = LOWER(:slug)
        """,
    )
    fun existsByAnyIdentifier(
        igdbId: Long?,
        rawgId: Long?,
        slug: String,
    ): Boolean

    /**
     * Batch lookup: Find all games by IGDB IDs.
     * Used for resolving IGDB's similar_games references.
     */
    @Query("SELECT g FROM Game g WHERE g.igdbId IN :igdbIds")
    fun findAllByIgdbIdIn(igdbIds: List<Long>): List<Game>
}

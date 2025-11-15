package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE LOWER(g.slug) = LOWER(:slug)")
    fun findBySlugIgnoreCase(slug: String): Game?

    fun findByIgdbId(igdbId: Long): Game?

    fun findByRawgId(rawgId: Long): Game?

    @Query(
        "SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.gameTags gt LEFT JOIN FETCH gt.tag WHERE g.rawgId IS NOT NULL",
    )
    fun findAllRawgGamesWithTags(): List<Game>

    @Query(
        "SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.gameTags gt LEFT JOIN FETCH gt.tag WHERE g.igdbId IS NOT NULL",
    )
    fun findAllIgdbGamesWithTags(): List<Game>

    /**
     * Merge RAWG game into IGDB game using pure SQL.
     * Does everything: copy unique tags, update IGDB game, delete source tags, delete RAWG game.
     */
    @Modifying
    @Query(
        value = """
            WITH source_data AS (
                SELECT rawg_id, rating, rating_count, website_url, background_image_url
                FROM games WHERE id = :sourceGameId
            ),
            tag_copy AS (
                INSERT INTO game_tags (game_id, tag_id, weight, created_at)
                SELECT :targetGameId, gt.tag_id, gt.weight, CURRENT_TIMESTAMP
                FROM game_tags gt
                WHERE gt.game_id = :sourceGameId
                AND NOT EXISTS (
                    SELECT 1 FROM game_tags gt2
                    WHERE gt2.game_id = :targetGameId AND gt2.tag_id = gt.tag_id
                )
                RETURNING tag_id
            ),
            delete_source_tags AS (
                DELETE FROM game_tags WHERE game_id = :sourceGameId
            ),
            game_update AS (
                UPDATE games g
                SET rawg_id = s.rawg_id,
                    rating = (
                        CASE
                            WHEN g.rating IS NOT NULL AND s.rating IS NOT NULL
                            THEN (g.rating + s.rating) / 2
                            ELSE COALESCE(g.rating, s.rating)
                        END
                    ),
                    rating_count = g.rating_count + s.rating_count,
                    website_url = COALESCE(g.website_url, s.website_url),
                    background_image_url = COALESCE(g.background_image_url, s.background_image_url),
                    updated_at = CURRENT_TIMESTAMP
                FROM source_data s
                WHERE g.id = :targetGameId
                RETURNING g.id
            )
            DELETE FROM games WHERE id = :sourceGameId
        """,
        nativeQuery = true,
    )
    fun mergeGameData(
        sourceGameId: Long,
        targetGameId: Long,
    )

    /**
     * Full-text search using PostgreSQL BM25-like ranking.
     * Searches across game name (weight A) and description (weight B).
     */
    @Query(
        value = """
            SELECT g.*,
                TS_RANK_CD(g.search_vector, PLAINTO_TSQUERY('english', :query)) AS rank
            FROM games g
            WHERE g.search_vector @@ PLAINTO_TSQUERY('english', :query)
            ORDER BY rank DESC
            LIMIT :limit OFFSET :offset
        """,
        nativeQuery = true,
    )
    fun searchGamesByQuery(
        query: String,
        limit: Int,
        offset: Int,
    ): List<Game>

    /**
     * Count total search results for pagination.
     */
    @Query(
        value = """
        SELECT COUNT(*)
        FROM games g
        WHERE g.search_vector @@ PLAINTO_TSQUERY('english', :query)
    """,
        nativeQuery = true,
    )
    fun countSearchResults(query: String): Long

    /**
     * Find game by slug for pretty URLs.
     */
    @Query("SELECT g FROM Game g WHERE LOWER(g.slug) = LOWER(:slug)")
    fun findBySlug(slug: String): Game?
}

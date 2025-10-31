package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameSimilarity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameSimilarityRepository : JpaRepository<GameSimilarity, Long> {
    /**
     * Delete all similarities for a specific game before recomputation.
     */
    @Modifying
    @Query("DELETE FROM GameSimilarity gs WHERE gs.id = :id")
    fun deleteByGameId(id: Long)

    /**
     * Find existing similarity records by type (avoid duplicates during ETL).
     */
    @Query(
        """
        SELECT gs FROM GameSimilarity gs
        WHERE gs.id = :id AND gs.similarityType = :type
        """,
    )
    fun findByGameIdAndType(
        id: Long,
        type: String,
    ): List<GameSimilarity>

    /**
     * Batch insert API-provided similarities (from IGDB's similar_games).
     * Returns count of inserted records.
     */
    @Modifying
    @Query(
        """
        INSERT INTO game_similarities (game_id, similar_game_id, similarity_score, similarity_type)
        VALUES (:gameId, :similarGameId, :score, 'api_provided')
        ON CONFLICT (game_id, similar_game_id) DO NOTHING
        """,
        nativeQuery = true,
    )
    fun insertApiProvidedSimilarity(
        gameId: Long,
        similarGameId: Long,
        score: Double,
    ): Int

    /**
     * Count similarities by type (for ETL reporting).
     */
    @Query("SELECT COUNT(gs) FROM GameSimilarity gs WHERE gs.similarityType = :type")
    fun countByType(type: String): Long
}

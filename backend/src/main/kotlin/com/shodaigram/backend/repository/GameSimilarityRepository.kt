package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameSimilarity
import com.shodaigram.backend.domain.entity.SimilarityType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameSimilarityRepository : JpaRepository<GameSimilarity, Long> {
    /**
     * Delete all similarities for a specific game.
     */
    @Modifying
    @Query("DELETE FROM GameSimilarity gs WHERE gs.game.id = :gameId")
    fun deleteByGameId(gameId: Long)

    /**
     * Find existing similarity records by type.
     */
    @Query(
        """
        SELECT gs FROM GameSimilarity gs
        WHERE gs.game.id = :gameId AND gs.similarityType = :type
        """,
    )
    fun findByGameIdAndType(
        gameId: Long,
        type: SimilarityType,
    ): List<GameSimilarity>

    /**
     * Check if a similarity relationship already exists.
     */
    @Query(
        """
        SELECT CASE WHEN COUNT(gs) > 0 THEN TRUE ELSE FALSE END
        FROM GameSimilarity gs
        WHERE gs.game.id = :gameId AND gs.similarGame.id = :similarGameId
        """,
    )
    fun existsByGamePair(
        gameId: Long,
        similarGameId: Long,
    ): Boolean

    /**
     * Count similarities by type (for ETL reporting).
     */
    @Query("SELECT COUNT(gs) FROM GameSimilarity gs WHERE gs.similarityType = :type")
    fun countByType(type: SimilarityType): Long
}

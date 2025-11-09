package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameSimilarity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameSimilarityRepository : JpaRepository<GameSimilarity, Long> {
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
}

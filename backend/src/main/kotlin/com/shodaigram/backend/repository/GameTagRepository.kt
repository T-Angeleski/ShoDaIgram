package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface GameTagRepository : JpaRepository<GameTag, Long> {
    /**
     * Insert a single game-tag association using native SQL.
     * Used to recreate tags after merge.
     */
    @Modifying
    @Query(
        """
        INSERT INTO game_tags (game_id, tag_id, weight, created_at)
        VALUES (:gameId, :tagId, :weight, CURRENT_TIMESTAMP)
        ON CONFLICT (game_id, tag_id) DO NOTHING
        """,
        nativeQuery = true,
    )
    fun insertGameTag(
        gameId: Long,
        tagId: Long,
        weight: BigDecimal,
    )
}

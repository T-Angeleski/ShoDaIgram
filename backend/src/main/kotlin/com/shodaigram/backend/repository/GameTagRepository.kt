package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameTagRepository : JpaRepository<GameTag, Long> {
    /**
     * Get all tag IDs associated with a game.
     */
    @Query("SELECT gt.tag.id FROM GameTag gt WHERE gt.game.id = :gameId")
    fun findTagIdsByGameId(gameId: Long): Set<Long>
}

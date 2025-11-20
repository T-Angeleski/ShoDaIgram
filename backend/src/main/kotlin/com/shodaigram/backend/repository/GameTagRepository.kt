package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.domain.entity.GameTag
import com.shodaigram.backend.domain.entity.Tag
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

    /**
     * Find all GameTag entities for a specific game (includes full Tag entity).
     */
    @Query("SELECT gt FROM GameTag gt JOIN FETCH gt.tag WHERE gt.game.id = :gameId")
    fun findByGameId(gameId: Long): List<GameTag>

    /**
     * Find all games associated with a tag (by tag ID).
     */
    @Query(
        """
        SELECT DISTINCT gt.game
        FROM GameTag gt
        WHERE gt.tag.id = :tagId
        ORDER BY gt.game.rating DESC
        """,
    )
    fun findGamesByTagId(tagId: Long): List<Game>

    /**
     * Find shared tags between two games for explainability.
     */
    @Query(
        """
        SELECT DISTINCT t
        FROM GameTag gt1
        JOIN GameTag gt2 ON gt1.tag.id = gt2.tag.id
        JOIN Tag t ON gt1.tag.id = t.id
        WHERE gt1.game.id = :gameId1
          AND gt2.game.id = :gameId2
        """,
    )
    fun findSharedTags(
        gameId1: Long,
        gameId2: Long,
    ): List<Tag>
}

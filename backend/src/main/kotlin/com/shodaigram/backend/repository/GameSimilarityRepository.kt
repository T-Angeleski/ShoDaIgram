package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameSimilarity
import com.shodaigram.backend.domain.entity.SimilarityType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    /**
     * Find top N similar games for a given name, ordered by similarity score
     */
    @Query(
        """
        SELECT gs FROM GameSimilarity gs
        WHERE gs.game.id = :gameId
        AND gs.similarityType = 'PRECOMPUTED_TF_IDF'
        ORDER BY gs.similarityScore DESC
        LIMIT :limit
        """,
    )
    fun findTopSimilarGames(
        @Param("gameId") gameId: Long,
        @Param("limit") limit: Int,
    ): List<GameSimilarity>

    /**
     * Find all similarities for a specific game
     */
    @Query(
        """
        SELECT gs FROM GameSimilarity gs
        WHERE gs.game.id = :gameId
        AND gs.similarityType = :similarityType
        """,
    )
    fun findByGameIdAndType(
        @Param("gameId") gameId: Long,
        @Param("similarityType") similarityType: SimilarityType,
    ): List<GameSimilarity>

    /**
     * Delete all precomputed TF-IDF similarities for a game.
     */
    @Modifying(clearAutomatically = true)
    @Query(
        """
        DELETE FROM GameSimilarity gs
        WHERE gs.game.id = :gameId
        AND gs.similarityType = 'PRECOMPUTED_TF_IDF'
        """,
    )
    fun deletePrecomputedSimilaritiesByGameId(
        @Param("gameId") gameId: Long,
    )

    /**
     * Delete all precomputed TF-IDF similarities for multiple games (bulk operation).
     * Used for batched similarity computation to reduce database round-trips.
     */
    @Modifying(clearAutomatically = true)
    @Query(
        """
        DELETE FROM GameSimilarity gs
        WHERE gs.game.id IN :gameIds
        AND gs.similarityType = 'PRECOMPUTED_TF_IDF'
        """,
    )
    fun deletePrecomputedSimilaritiesByGameIds(
        @Param("gameIds") gameIds: List<Long>,
    )

    /**
     * Delete all precomputed TF-IDF similarities.
     */
    @Modifying
    @Query(
        """
        DELETE FROM GameSimilarity gs
        WHERE gs.similarityType = 'PRECOMPUTED_TF_IDF'
        """,
    )
    fun deleteAllPrecomputedSimilarities()

    /**
     * Count total precomputed similarities.
     */
    @Query(
        """
        SELECT COUNT(gs) FROM GameSimilarity gs
        WHERE gs.similarityType = 'PRECOMPUTED_TF_IDF'
        """,
    )
    fun countPrecomputedSimilarities(): Long
}

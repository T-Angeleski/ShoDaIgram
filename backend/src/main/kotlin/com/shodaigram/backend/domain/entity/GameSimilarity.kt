package com.shodaigram.backend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "game_similarities")
data class GameSimilarity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "similar_game_id", nullable = false)
    val similarGame: Game,
    @Column(name = "similarity_score", nullable = false, precision = 5, scale = 4)
    val similarityScore: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(name = "similarity_type", nullable = false, length = 20)
    val similarityType: SimilarityType = SimilarityType.API_PROVIDED,
    @Column(name = "computed_at", nullable = false)
    val computedAt: LocalDateTime = LocalDateTime.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameSimilarity) return false
        return game.id == other.game.id && similarGame.id == other.similarGame.id
    }

    override fun hashCode(): Int = 31 * (game.id?.hashCode() ?: 0) + (similarGame.id?.hashCode() ?: 0)
}

enum class SimilarityType {
    PRECOMPUTED_TF_IDF,
    API_PROVIDED,
    TAG_BASED,
}

package com.shodaigram.backend.domain.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "game_tags")
@EntityListeners(AuditingEntityListener::class)
data class GameTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag,

    @Column(name = "weight", precision = 3, scale = 2)
    val weight: BigDecimal = BigDecimal.ONE,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameTag) return false
        return game.id == other.game.id && tag.id == other.tag.id
    }

    override fun hashCode(): Int = 31 * (game.id?.hashCode() ?: 0) + (tag.id?.hashCode() ?: 0)
}

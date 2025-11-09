package com.shodaigram.backend.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["normalized_name", "category"])],
)
@EntityListeners(AuditingEntityListener::class)
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "normalized_name", nullable = false)
    val normalizedName: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val category: TagCategory,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true)
    val gameTags: MutableSet<GameTag> = mutableSetOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tag) return false
        return normalizedName == other.normalizedName && category == other.category
    }

    override fun hashCode(): Int = 31 * normalizedName.hashCode() + category.hashCode()
}

enum class TagCategory {
    GENRE,
    PLATFORM,
    THEME,
    GAME_MODE,
    FRANCHISE,
    PLAYER_PERSPECTIVE,
    DEVELOPER,
    PUBLISHER,
    KEYWORD,
}

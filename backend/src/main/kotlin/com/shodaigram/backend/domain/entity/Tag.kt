package com.shodaigram.backend.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["normalized_name", "category"])]
)
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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime? = null

//    val gametags
)

enum class TagCategory {
    GENRE,
    PLATFORM,
    THEME,
    GAME_MODE,
    FRANCHISE,
    PLAYER_PERSPECTIVE,
    DEVELOPER,
    PUBLISHER,
    KEYWORD
}

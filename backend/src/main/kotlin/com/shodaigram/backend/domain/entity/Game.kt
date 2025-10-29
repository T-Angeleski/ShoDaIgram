package com.shodaigram.backend.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "games")
@EntityListeners(AuditingEntityListener::class)
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "igdb_id", unique = true)
    val igdbId: Long? = null,
    @Column(name = "rawg_id", unique = true)
    val rawgId: Long? = null,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false, unique = true)
    val slug: String,
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    @Column(name = "release_date")
    val releaseDate: LocalDate? = null,
    @Column(precision = 5, scale = 2)
    val rating: BigDecimal? = null,
    @Column(name = "rating_count")
    val ratingCount: Int = 0,
    @Column(name = "background_image_url", columnDefinition = "TEXT")
    val backgroundImageUrl: String? = null,
    @Column(name = "website_url", columnDefinition = "TEXT")
    val websiteUrl: String? = null,
    @Column(name = "search_vector", columnDefinition = "TSVECTOR", insertable = false, updatable = false)
    val searchVector: String? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], orphanRemoval = true)
    val gameTags: MutableSet<GameTag> = mutableSetOf(),
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], orphanRemoval = true)
    val similarities: MutableSet<GameSimilarity> = mutableSetOf(),
) {
    fun addTag(
        tag: Tag,
        weight: BigDecimal = BigDecimal.ONE,
    ) {
        val gameTag = GameTag(game = this, tag = tag, weight = weight)
        gameTags.add(gameTag)
    }

    fun removeTag(tag: Tag) {
        gameTags.removeIf { it.tag == tag }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Game) return false
        return slug == other.slug
    }

    override fun hashCode(): Int {
        return slug.hashCode()
    }
}

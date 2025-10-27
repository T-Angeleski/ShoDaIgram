package com.shodaigram.backend.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "games")
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

    @Column(name = "search_vector", columnDefinition = "TSVECTOR")
    val searchVector: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null

//    val gameTags
//    val similarities
)

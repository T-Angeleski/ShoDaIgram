package com.shodaigram.backend.domain.dto.etl

import com.fasterxml.jackson.annotation.JsonProperty
import com.shodaigram.backend.domain.entity.Game
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

/**
 * Maps to IGDB API JSON structure
 */
data class IgdbGameDto(
    @JsonProperty("igdb_id")
    val igdbId: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("summary")
    val summary: String?,
    @JsonProperty("storyline")
    val storyline: String?,
    @JsonProperty("url")
    val url: String?,
    @JsonProperty("cover_url")
    val coverUrl: String?,
    @JsonProperty("first_release_date")
    val firstReleaseDate: LocalDate?,
    @JsonProperty("rating")
    val rating: Double?,
    @JsonProperty("rating_count")
    val ratingCount: Int?,
    @JsonProperty("total_rating")
    val totalRating: Double?,
    @JsonProperty("total_rating_count")
    val totalRatingCount: Int?,
    @JsonProperty("genres")
    val genres: List<String> = emptyList(),
    @JsonProperty("platforms")
    val platforms: List<String> = emptyList(),
    @JsonProperty("themes")
    val themes: List<String> = emptyList(),
    @JsonProperty("game_modes")
    val gameModes: List<String> = emptyList(),
    @JsonProperty("franchises")
    val franchises: List<String> = emptyList(),
    @JsonProperty("keywords")
    val keywords: List<String> = emptyList(),
    @JsonProperty("player_perspectives")
    val playerPerspectives: List<String> = emptyList(),
    @JsonProperty("game_engines")
    val gameEngines: List<String> = emptyList(),
    @JsonProperty("similar_games")
    val similarGames: List<String> = emptyList(),
    @JsonProperty("developers")
    val developers: List<String> = emptyList(),
    @JsonProperty("publishers")
    val publishers: List<String> = emptyList(),
    @JsonProperty("age_ratings")
    val ageRatings: Map<String, Any> = emptyMap(),
    @JsonProperty("collection")
    val collection: List<String> = emptyList(),
    @JsonProperty("fetched_at")
    val fetchedAt: OffsetDateTime,
    @JsonProperty("data_source")
    val dataSource: String = "igdb",
) {
    fun toEntity() = Game(
        name = this.name,
        slug = this.slug,
        description = this.summary ?: this.storyline,
        releaseDate = this.firstReleaseDate,
        rating = this.totalRating?.toBigDecimal(),
        backgroundImageUrl = this.coverUrl,
        igdbId = this.igdbId,
        rawgId = null,
        updatedAt = LocalDateTime.now(),
    )
}

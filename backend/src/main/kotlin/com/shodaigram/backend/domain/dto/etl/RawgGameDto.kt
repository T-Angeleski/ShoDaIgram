package com.shodaigram.backend.domain.dto.etl

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * Maps to RAWG API JSON structure
 */
data class RawgGameDto(
    @JsonProperty("rawg_id")
    val rawgId: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("released")
    val released: LocalDate?,
    @JsonProperty("rating")
    val rating: Double?,
    @JsonProperty("rating_top")
    val ratingTop: Int?,
    @JsonProperty("ratings_count")
    val ratingsCount: Int?,
    @JsonProperty("metacritic")
    val metacritic: Int?,
    @JsonProperty("description_raw")
    val descriptionRaw: String?,
    @JsonProperty("background_image")
    val backgroundImage: String?,
    @JsonProperty("website")
    val website: String?,
    @JsonProperty("playtime")
    val playtime: Int?,
    @JsonProperty("achievements_count")
    val achievementsCount: Int?,
    @JsonProperty("genres")
    val genres: List<String> = emptyList(),
    @JsonProperty("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @JsonProperty("platforms")
    val platforms: List<String> = emptyList(),
    @JsonProperty("developers")
    val developers: List<String> = emptyList(),
    @JsonProperty("publishers")
    val publishers: List<String> = emptyList(),
    @JsonProperty("tags")
    val tags: List<String> = emptyList(),
    @JsonProperty("creators_count")
    val creatorsCount: Int?,
    @JsonProperty("additions_count")
    val additionsCount: Int?,
    @JsonProperty("game_series_count")
    val gameSeriesCount: Int?,
    @JsonProperty("user_game")
    val userGame: String?,
    @JsonProperty("updated")
    val updated: OffsetDateTime?,
    @JsonProperty("fetched_at")
    val fetchedAt: OffsetDateTime,
    @JsonProperty("data_source")
    val dataSource: String = "rawg",
)

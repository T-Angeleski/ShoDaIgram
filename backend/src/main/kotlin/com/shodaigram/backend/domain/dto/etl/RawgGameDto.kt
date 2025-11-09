package com.shodaigram.backend.domain.dto.etl

import com.fasterxml.jackson.annotation.JsonProperty
import com.shodaigram.backend.domain.entity.Game
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class RawgGameDto(
    @JsonProperty("rawg_id") val rawgId: Long,
    @JsonProperty("name") val name: String,
    @JsonProperty("slug") val slug: String,
    @JsonProperty("released") val released: LocalDate?,
    @JsonProperty("rating") val rating: Double?,
    @JsonProperty("rating_top") val ratingTop: Int?,
    @JsonProperty("ratings_count") val ratingsCount: Int?,
    @JsonProperty("metacritic") val metacritic: Int?,
    @JsonProperty("description_raw") val descriptionRaw: String?,
    @JsonProperty("background_image") val backgroundImage: String?,
    @JsonProperty("website") val website: String?,
    @JsonProperty("playtime") val playtime: Int?,
    @JsonProperty("genres") val genres: List<String> = emptyList(),
    @JsonProperty("platforms") val platforms: List<String> = emptyList(),
    @JsonProperty("developers") val developers: List<String> = emptyList(),
    @JsonProperty("publishers") val publishers: List<String> = emptyList(),
    @JsonProperty("tags") val tags: List<String> = emptyList(),
    @JsonProperty("fetched_at") val fetchedAt: OffsetDateTime,
) {
    fun toEntity() =
        Game(
            name = this.name,
            slug = this.slug,
            description = this.descriptionRaw,
            releaseDate = this.released,
            rating = this.rating?.let { normalizeRawgRating(it) },
            ratingCount = this.ratingsCount ?: 0,
            backgroundImageUrl = this.backgroundImage,
            websiteUrl = this.website,
            igdbId = null,
            rawgId = this.rawgId,
            updatedAt = LocalDateTime.now(),
        )

    /**
     * Normalize RAWG rating from 0-5 scale to 0-10 scale for database consistency
     */
    private fun normalizeRawgRating(rawgRating: Double): java.math.BigDecimal {
        val normalized = (rawgRating * 2.0).coerceIn(0.0, 10.0)
        return normalized.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
    }
}

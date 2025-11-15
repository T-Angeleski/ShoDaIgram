package com.shodaigram.backend.domain.dto.game

import com.shodaigram.backend.domain.entity.Game
import java.math.BigDecimal

/**
 * Detailed DTO for single game view.
 * Includes full game information with tags and optional similar games preview.
 */
data class GameDetailDto(
    val id: Long,
    val name: String,
    val slug: String,
    val description: String?,
    val rating: BigDecimal?,
    val ratingCount: Int,
    val releaseDate: String?,
    val backgroundImageUrl: String?,
    val websiteUrl: String?,
    val tags: List<TagDto>,
    val tagsByCategory: Map<String, List<TagDto>>,
) {
    companion object {
        fun fromEntity(
            game: Game,
            tags: List<TagDto>,
        ) = GameDetailDto(
            id = game.id!!,
            name = game.name,
            slug = game.slug,
            description = game.description,
            rating = game.rating,
            ratingCount = game.ratingCount,
            releaseDate = game.releaseDate?.toString(),
            backgroundImageUrl = game.backgroundImageUrl,
            websiteUrl = game.websiteUrl,
            tags = tags,
            tagsByCategory = tags.groupBy { it.category.name },
        )
    }
}

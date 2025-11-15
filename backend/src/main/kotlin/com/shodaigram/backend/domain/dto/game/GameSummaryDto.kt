package com.shodaigram.backend.domain.dto.game

import com.shodaigram.backend.domain.entity.Game
import java.math.BigDecimal

/**
 * DTO for game lists and search results.
 * Contains essential information for displaying game cards.
 */
data class GameSummaryDto(
    val id: Long,
    val name: String,
    val slug: String,
    val rating: BigDecimal?,
    val ratingCount: Int,
    val releaseDate: String?,
    val backgroundImageUrl: String?,
) {
    companion object {
        fun fromEntity(game: Game) =
            GameSummaryDto(
                id = game.id!!,
                name = game.name,
                slug = game.slug,
                rating = game.rating,
                ratingCount = game.ratingCount,
                releaseDate = game.releaseDate?.toString(),
                backgroundImageUrl = game.backgroundImageUrl,
            )
    }
}

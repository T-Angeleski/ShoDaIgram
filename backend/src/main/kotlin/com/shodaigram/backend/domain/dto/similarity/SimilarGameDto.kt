package com.shodaigram.backend.domain.dto.similarity

import com.shodaigram.backend.domain.entity.SimilarityType
import java.math.BigDecimal

/**
 * Basic DTO for similar game recommendations.
 */
data class SimilarGameDto(
    val gameId: Long,
    val name: String,
    val slug: String,
    val rating: BigDecimal?,
    val ratingCount: Int?,
    val releaseDate: String?,
    val backgroundImageUrl: String?,
    val similarityScore: BigDecimal,
    val similarityType: SimilarityType,
)

/**
 * Extended DTO with explainability for similar game recommendations
 */
data class SimilarGameWithReasonsDto(
    val gameId: Long,
    val name: String,
    val slug: String,
    val rating: BigDecimal?,
    val ratingCount: Int?,
    val releaseDate: String?,
    val backgroundImageUrl: String?,
    val similarityScore: BigDecimal,
    val similarityType: SimilarityType,
    val matchReasons: List<MatchReason>,
)

data class MatchReason(
    val type: MatchReasonType,
    val details: String,
    val contribution: BigDecimal,
)

/**
 * Response for TF_IDF similarity computation job
 */
data class SimilarityComputationResponse(
    val status: String,
    val gamesProcessed: Int,
    val similaritiesComputed: Int,
    val durationMs: Long,
    val errorMessage: String? = null,
) {
    companion object {
        fun emptyComputationResponse() =
            SimilarityComputationResponse(
                status = "COMPLETED",
                gamesProcessed = 0,
                similaritiesComputed = 0,
                durationMs = 0,
                errorMessage = "No games found in database",
            )
    }
}

enum class MatchReasonType {
    GENRE_MATCH,
    THEME_MATCH,
    FRANCHISE_MATCH,
    DESCRIPTION_SIMILARITY,
}

/**
 * Unified response wrapper for game recommendations.
 * Supports both basic recommendations and recommendations with explainability.
 */
data class RecommendationResponse(
    val recommendations: List<SimilarGameDto>,
    val detailedRecommendations: List<SimilarGameWithReasonsDto>?,
    val metadata: RecommendationMetadata,
) {
    companion object {
        fun basic(
            recommendations: List<SimilarGameDto>,
            gameId: Long,
            limit: Int,
        ) = RecommendationResponse(
            recommendations = recommendations,
            detailedRecommendations = null,
            metadata =
                RecommendationMetadata(
                    sourceGameId = gameId,
                    algorithm = "TF-IDF",
                    requestedLimit = limit,
                    returnedCount = recommendations.size,
                    explainability = false,
                ),
        )

        fun detailed(
            recommendations: List<SimilarGameWithReasonsDto>,
            gameId: Long,
            limit: Int,
        ) = RecommendationResponse(
            recommendations = recommendations.map { it.toBasicDto() },
            detailedRecommendations = recommendations,
            metadata =
                RecommendationMetadata(
                    sourceGameId = gameId,
                    algorithm = "TF-IDF",
                    requestedLimit = limit,
                    returnedCount = recommendations.size,
                    explainability = true,
                ),
        )
    }
}

/**
 * Metadata about the recommendation request and computation.
 */
data class RecommendationMetadata(
    val sourceGameId: Long,
    val algorithm: String,
    val requestedLimit: Int,
    val returnedCount: Int,
    val explainability: Boolean,
)

/**
 * Extension to convert detailed DTO to basic DTO
 */
private fun SimilarGameWithReasonsDto.toBasicDto() =
    SimilarGameDto(
        gameId = gameId,
        name = name,
        slug = slug,
        rating = rating,
        ratingCount = ratingCount,
        releaseDate = releaseDate,
        backgroundImageUrl = backgroundImageUrl,
        similarityScore = similarityScore,
        similarityType = similarityType,
    )

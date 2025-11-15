package com.shodaigram.backend.controller

import com.shodaigram.backend.service.similarity.TfIdfSimilarityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game Recommendations", description = "Game recommendation endpoints based on TF-IDF similarity")
class SimilarGamesController(
    private val tfIdfSimilarityService: TfIdfSimilarityService,
) {
    @GetMapping("/{id}/recommendations")
    @Operation(
        summary = "Get game recommendations",
        description = """
            Returns games similar to the specified game based on TF-IDF content similarity.

            Optionally include explainability to show why games are recommended:
            - Shared genres, themes, franchises
            - Description similarity analysis

            Example: `/api/games/123/recommendations?limit=10&explainability=true`
        """,
    )
    fun getRecommendations(
        @Parameter(description = "Game ID") @PathVariable id: Long,
        @Parameter(description = "Maximum number of results") @RequestParam(defaultValue = "10") limit: Int,
        @Parameter(description = "Include match reasons") @RequestParam(defaultValue = "false") explainability: Boolean,
    ): ResponseEntity<*> {
        return if (explainability) {
            val recommendations = tfIdfSimilarityService.getSimilarGamesWithReasons(id, limit)
            ResponseEntity.ok(recommendations)
        } else {
            val recommendations = tfIdfSimilarityService.getSimilarGames(id, limit)
            ResponseEntity.ok(recommendations)
        }
    }
}

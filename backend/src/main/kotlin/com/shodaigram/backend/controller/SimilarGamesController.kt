package com.shodaigram.backend.controller

import com.shodaigram.backend.domain.dto.similarity.SimilarGameDto
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
@Tag(name = "Similar Games", description = "Game similarity and recommendation endpoints")
class SimilarGamesController(
    private val tfIdfSimilarityService: TfIdfSimilarityService,
) {
    @GetMapping("/{id}/similar")
    @Operation(
        summary = "Get similar games",
        description = "Returns games similar to the specified game based on TF-IDF content similarity",
    )
    fun getSimilarGames(
        @Parameter(description = "Game ID") @PathVariable id: Long,
        @Parameter(description = "Maximum number of results") @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<List<SimilarGameDto>> {
        val similarGames = tfIdfSimilarityService.getSimilarGames(id, limit)
        return ResponseEntity.ok(similarGames)
    }
}

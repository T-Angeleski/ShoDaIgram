package com.shodaigram.backend.controller

import com.shodaigram.backend.domain.dto.game.GameFilterResponse
import com.shodaigram.backend.service.game.GameFilterService
import com.shodaigram.backend.util.StringUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game Filtering", description = "Advanced game filtering and discovery endpoints")
class GameFilterController(
    private val gameFilterService: GameFilterService,
) {
    @GetMapping("/filter")
    @Operation(
        summary = "Filter games by multiple criteria",
        description = """
            Advanced filtering with multiple criteria (AND logic for all filters).

            Supports:
            - Tags (comma-separated) - game must have ALL specified tags
            - Rating range (minRating, maxRating)
            - Release year range (minYear, maxYear)
            - Sorting and pagination

            Examples:
            - `/api/games/filter?tags=action,rpg&minRating=8`
            - `/api/games/filter?tags=souls-like&minYear=2020&sort=rating,desc`
            - `/api/games/filter?minRating=9&maxYear=2024&page=0&size=20`
        """,
    )
    fun filterGames(
        @Parameter(description = "Comma-separated tag names (case-insensitive)")
        @RequestParam(required = false)
        tags: String?,
        @Parameter(description = "Minimum rating (0-10)")
        @RequestParam(required = false)
        minRating: Double?,
        @Parameter(description = "Maximum rating (0-10)")
        @RequestParam(required = false)
        maxRating: Double?,
        @Parameter(description = "Minimum release year")
        @RequestParam(required = false)
        minYear: Int?,
        @Parameter(description = "Maximum release year")
        @RequestParam(required = false)
        maxYear: Int?,
        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "Results per page")
        @RequestParam(defaultValue = "20")
        size: Int,
        @Parameter(description = "Sort field and direction (e.g., 'rating,desc')")
        @RequestParam(defaultValue = "rating,desc")
        sort: String,
    ): ResponseEntity<GameFilterResponse> {
        val tagList = tags?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
        val sortParams = StringUtils.parseSortParam(sort)
        val pageable = PageRequest.of(page, size, sortParams)

        val response =
            gameFilterService.filterGames(
                tags = tagList,
                minRating = minRating,
                maxRating = maxRating,
                minYear = minYear,
                maxYear = maxYear,
                pageable = pageable,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-tags")
    @Operation(
        summary = "Find games by multiple tags (intersection)",
        description = """
            Find games that have ALL specified tags (AND logic).

            Useful for precise discovery queries like:
            - "Action RPGs" → tags=action,rpg
            - "Open world fantasy games" → tags=open-world,fantasy

            Example: `/api/games/by-tags?tags=action,rpg,open-world&page=0&size=20`
        """,
    )
    fun findGamesByTags(
        @Parameter(description = "Comma-separated tag names", required = true)
        @RequestParam
        tags: String,
        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "Results per page")
        @RequestParam(defaultValue = "20")
        size: Int,
        @Parameter(description = "Sort field and direction")
        @RequestParam(defaultValue = "rating,desc")
        sort: String,
    ): ResponseEntity<GameFilterResponse> {
        val tagList =
            tags.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

        val sortParams = StringUtils.parseSortParam(sort)
        val pageable = PageRequest.of(page, size, sortParams)

        val response = gameFilterService.findGamesByAllTags(tagList, pageable)
        return ResponseEntity.ok(response)
    }
}

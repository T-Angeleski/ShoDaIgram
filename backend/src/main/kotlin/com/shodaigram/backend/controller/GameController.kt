package com.shodaigram.backend.controller

import com.shodaigram.backend.domain.dto.game.GameDetailDto
import com.shodaigram.backend.domain.dto.game.GamePageDto
import com.shodaigram.backend.domain.dto.game.SearchResultDto
import com.shodaigram.backend.service.game.GameSearchService
import com.shodaigram.backend.service.game.GameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/games")
@Tag(name = "Games", description = "Game retrieval and search endpoints")
class GameController(
    private val gameService: GameService,
    private val gameSearchService: GameSearchService,
) {
    @GetMapping("/{id}")
    @Operation(
        summary = "Get game by ID",
        description = "Returns detailed game information including tags grouped by category",
    )
    fun getGameById(
        @Parameter(description = "Game ID") @PathVariable id: Long,
    ): ResponseEntity<GameDetailDto> {
        val game = gameService.getGameById(id)
        return ResponseEntity.ok(game)
    }

    @GetMapping("/slug/{slug}")
    @Operation(
        summary = "Get game by slug",
        description = "Returns detailed game information using URL-friendly slug identifier",
    )
    fun getGameBySlug(
        @Parameter(description = "Game slug (e.g., 'the-witcher-3-wild-hunt')") @PathVariable slug: String,
    ): ResponseEntity<GameDetailDto> {
        val game = gameService.getGameBySlug(slug)
        return ResponseEntity.ok(game)
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search games",
        description = """
            Full-text search using PostgreSQL BM25-like ranking.
            Searches across game names and descriptions with relevance scoring.

            Examples:
            - `/api/games/search?query=dark souls`
            - `/api/games/search?query=open world rpg&page=0&size=20`
        """,
    )
    fun searchGames(
        @Parameter(description = "Search query") @RequestParam query: String,
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Results per page") @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<SearchResultDto> {
        val results = gameSearchService.searchGames(query, page, size)
        return ResponseEntity.ok(results)
    }

    @GetMapping
    @Operation(
        summary = "Browse all games",
        description = """
            Get paginated list of all games with optional sorting.

            Sort options:
            - `rating,desc` - Highest rated first (default)
            - `rating,asc` - Lowest rated first
            - `releaseDate,desc` - Newest first
            - `releaseDate,asc` - Oldest first
            - `name,asc` - Alphabetical

            Example: `/api/games?page=0&size=20&sort=rating,desc`
        """,
    )
    fun getAllGames(
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Results per page") @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "Sort field and direction") @RequestParam(defaultValue = "rating,desc") sort: String,
    ): ResponseEntity<GamePageDto> {
        val sortParams = parseSortParam(sort)
        val pageable = PageRequest.of(page, size, sortParams)
        val games = gameService.getAllGames(pageable)
        return ResponseEntity.ok(games)
    }

    private fun parseSortParam(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val field = parts.getOrNull(0) ?: "rating"
        val direction = parts.getOrNull(1)?.uppercase() ?: "DESC"

        return if (direction == "ASC") {
            Sort.by(Sort.Order.asc(field))
        } else {
            Sort.by(Sort.Order.desc(field))
        }
    }
}

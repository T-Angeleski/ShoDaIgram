package com.shodaigram.backend.controller

import com.shodaigram.backend.domain.dto.tag.TagGamesResponseDto
import com.shodaigram.backend.domain.entity.TagCategory
import com.shodaigram.backend.service.tag.TagService
import com.shodaigram.backend.util.EtlConstants
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
@RequestMapping("/api/tags")
@Tag(name = "Tags", description = "Tag browsing and discovery endpoints")
class TagController(
    private val tagService: TagService,
) {
    @GetMapping
    @Operation(
        summary = "Browse all tags",
        description = """
            Get all tags with game counts, optionally filtered by category.

            Categories: GENRE, THEME, PLATFORM, GAME_MODE, FRANCHISE, PLAYER_PERSPECTIVE,
                        DEVELOPER, PUBLISHER, KEYWORD

            Examples:
            - `/api/games/tags` - All tags
            - `/api/games/tags?category=GENRE` - Only genre tags
            - `/api/games/tags?category=DEVELOPER&page=0&size=50` - Developers with pagination
        """,
    )
    fun getAllTags(
        @Parameter(description = "Filter by category")
        @RequestParam(required = false)
        category: TagCategory?,
        @Parameter(description = "Search term for filtering tags by name")
        @RequestParam(required = false)
        search: String?,
        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "Results per page")
        @RequestParam(defaultValue = "100")
        size: Int,
        @Parameter(description = "Maximum results (for search/autocomplete)")
        @RequestParam(required = false)
        limit: Int?,
    ): ResponseEntity<*> {
        return if (search != null) {
            val tags = tagService.searchTags(search, limit ?: EtlConstants.ValidationConstants.DEFAULT_TAG_LIMIT)
            ResponseEntity.ok(tags)
        } else {
            val response = tagService.getAllTags(category, page, size)
            ResponseEntity.ok(response)
        }
    }

    @GetMapping("/{tagName}/games")
    @Operation(
        summary = "Get games by tag",
        description = """
            Find all games associated with a specific tag.

            Tag lookup is case-insensitive and uses normalized names.

            Examples:
            - `/api/games/tags/souls-like/games` - All Souls-like games
            - `/api/games/tags/dark-fantasy/games?page=0&size=20` - Dark fantasy with pagination
            - `/api/games/tags/rockstar-games/games` - Games by Rockstar Games
        """,
    )
    fun getGamesByTag(
        @Parameter(description = "Tag name (case-insensitive)")
        @PathVariable
        tagName: String,
        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "Results per page")
        @RequestParam(defaultValue = "20")
        size: Int,
    ): ResponseEntity<TagGamesResponseDto> {
        val response = tagService.getGamesByTag(tagName, page, size)
        return ResponseEntity.ok(response)
    }
}

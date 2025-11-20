package com.shodaigram.backend.service.game

import com.shodaigram.backend.domain.dto.game.GameSearchDto
import com.shodaigram.backend.domain.dto.game.SearchResultDto
import com.shodaigram.backend.repository.GameRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for game search using full-text search
 */
interface GameSearchService {
    /**
     * Search games by query string with pagination
     *
     * @param query Search query (natural language)
     * @param page Page number (0-indexed)
     * @param size Results per page
     * @return Search results with BM25 relevance scores
     */
    fun searchGames(
        query: String,
        page: Int = 0,
        size: Int = 20,
    ): SearchResultDto
}

@Service
class GameSearchServiceImpl(
    private val gameRepository: GameRepository,
) : GameSearchService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun searchGames(
        query: String,
        page: Int,
        size: Int,
    ): SearchResultDto {
        val sanitizedQuery = query.trim()

        if (sanitizedQuery.isBlank()) return emptySearchResult(query, page, size)

        logger.debug("Searching games with query: '$sanitizedQuery', page: $page, size: $size")

        val offset = page * size
        val games = gameRepository.searchGamesByQuery(sanitizedQuery, size, offset)
        val totalResults = gameRepository.countSearchResults(sanitizedQuery)

        val searchResults =
            games.mapIndexed { index, game ->
                GameSearchDto(
                    id = game.id!!,
                    name = game.name,
                    slug = game.slug,
                    description = game.description,
                    rating = game.rating,
                    ratingCount = game.ratingCount,
                    releaseDate = game.releaseDate?.toString(),
                    backgroundImageUrl = game.backgroundImageUrl,
                    searchRank = calculateRank(totalResults, offset + index),
                )
            }

        val totalPages = ((totalResults + size - 1) / size).toInt()

        logger.info("Search completed: query='$sanitizedQuery', found=$totalResults results")

        return SearchResultDto(
            games = searchResults,
            query = sanitizedQuery,
            totalResults = totalResults.toInt(),
            page = page,
            pageSize = size,
            totalPages = totalPages,
        )
    }

    private fun emptySearchResult(
        query: String,
        page: Int,
        size: Int,
    ) = SearchResultDto(
        games = emptyList(),
        query = query,
        totalResults = 0,
        page = page,
        pageSize = size,
        totalPages = 0,
    )

    /**
     * Calculate normalized rank score for display purposes.
     * Higher rank for results appearing earlier in the list.
     */
    private fun calculateRank(
        totalResults: Long,
        position: Int,
    ): Double {
        if (totalResults == 0L) return 0.0
        return 1.0 - (position.toDouble() / totalResults.toDouble())
    }
}

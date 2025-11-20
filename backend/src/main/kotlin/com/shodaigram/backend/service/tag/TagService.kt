package com.shodaigram.backend.service.tag

import com.shodaigram.backend.domain.dto.game.GameSummaryDto
import com.shodaigram.backend.domain.dto.tag.TagGamesResponseDto
import com.shodaigram.backend.domain.dto.tag.TagInfo
import com.shodaigram.backend.domain.dto.tag.TagListResponse
import com.shodaigram.backend.domain.dto.tag.TagSummaryDto
import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.domain.entity.TagCategory
import com.shodaigram.backend.exception.TagNotFoundException
import com.shodaigram.backend.repository.GameTagRepository
import com.shodaigram.backend.repository.TagRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for tag browsing, discovery, and tag-based game retrieval.
 */
interface TagService {
    /**
     * Get all tags with game counts, optionally filtered by category.
     *
     * @param category Optional category filter
     * @param page Page number (0-indexed)
     * @param size Results per page
     * @return Paginated list of tags with game counts
     */
    fun getAllTags(
        category: TagCategory?,
        page: Int = 0,
        size: Int = 100,
    ): TagListResponse

    /**
     * Search tags by name with game counts.
     *
     * @param searchTerm Search query
     * @param limit Maximum number of results
     * @return List of matching tags with game counts
     */
    fun searchTags(
        searchTerm: String,
        limit: Int = 20,
    ): List<TagSummaryDto>

    /**
     * Get all games associated with a specific tag.
     *
     * @param tagName Tag name (case-insensitive)
     * @param page Page number (0-indexed)
     * @param size Results per page
     * @return Games with the specified tag
     */
    fun getGamesByTag(
        tagName: String,
        page: Int = 0,
        size: Int = 20,
    ): TagGamesResponseDto
}

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
    private val gameTagRepository: GameTagRepository,
) : TagService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun getAllTags(
        category: TagCategory?,
        page: Int,
        size: Int,
    ): TagListResponse {
        logger.debug("Fetching tags - category: $category, page: $page, size: $size")

        val results =
            if (category != null) {
                tagRepository.findByCategoryWithGameCounts(category)
            } else {
                tagRepository.findAllWithGameCounts()
            }

        val tags = results.map { mapToTagSummaryDto(it) }

        val paginatedTags =
            tags
                .drop(page * size)
                .take(size)

        val totalPages = (tags.size + size - 1) / size

        logger.info("Fetched ${tags.size} tags (category: $category)")

        return TagListResponse(
            tags = paginatedTags,
            totalTags = tags.size.toLong(),
            page = page,
            pageSize = size,
            totalPages = totalPages,
        )
    }

    @Transactional(readOnly = true)
    override fun searchTags(
        searchTerm: String,
        limit: Int,
    ): List<TagSummaryDto> {
        if (searchTerm.isBlank()) {
            return emptyList()
        }

        logger.debug("Searching tags with term: '$searchTerm', limit: $limit")

        val results = tagRepository.searchByNameWithGameCounts(searchTerm)
        val tags = results.map { mapToTagSummaryDto(it) }.take(limit)

        logger.info("Found ${tags.size} tags matching '$searchTerm'")

        return tags
    }

    @Transactional(readOnly = true)
    override fun getGamesByTag(
        tagName: String,
        page: Int,
        size: Int,
    ): TagGamesResponseDto {
        val normalizedName = tagName.lowercase().trim()
        logger.debug("Fetching games for tag: '$normalizedName', page: $page, size: $size")

        val tag =
            tagRepository.findByNormalizedName(normalizedName)
                ?: throw TagNotFoundException("Tag not found: $tagName")

        val allGames = gameTagRepository.findGamesByTagId(tag.id!!)
        val gameCount = allGames.size.toLong()

        val paginatedGames =
            allGames
                .drop(page * size)
                .take(size)
                .map { GameSummaryDto.fromEntity(it) }

        val totalPages = (gameCount.toInt() + size - 1) / size

        logger.info("Found $gameCount games for tag '${tag.name}'")

        return TagGamesResponseDto(
            tag =
                TagInfo(
                    name = tag.name,
                    normalizedName = tag.normalizedName,
                    category = tag.category,
                ),
            games = paginatedGames,
            page = page,
            pageSize = size,
            totalResults = gameCount,
            totalPages = totalPages,
        )
    }

    private fun mapToTagSummaryDto(result: Array<Any>): TagSummaryDto {
        val tag = result[0] as Tag
        val count = (result[1] as Long)
        return TagSummaryDto.fromEntity(tag, count)
    }
}

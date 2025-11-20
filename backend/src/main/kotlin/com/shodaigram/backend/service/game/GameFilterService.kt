package com.shodaigram.backend.service.game

import com.shodaigram.backend.domain.dto.game.AppliedFilters
import com.shodaigram.backend.domain.dto.game.GameFilterResponse
import com.shodaigram.backend.domain.dto.game.GameSummaryDto
import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.exception.InvalidFilterException
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.repository.GameSpecifications
import com.shodaigram.backend.util.EtlConstants
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for game filtering with multiple criteria
 */
interface GameFilterService {
    /**
     * Filter games by multiple criteria (tags, rating, year).
     *
     * @param tags List of tag normalized names (AND logic - game must have ALL)
     * @param minRating Minimum rating threshold
     * @param maxRating Maximum rating threshold
     * @param minYear Minimum release year
     * @param maxYear Maximum release year
     * @param pageable Pagination and sorting
     * @return Filtered games with applied filter metadata
     */
    fun filterGames(
        tags: List<String>?,
        minRating: Double?,
        maxRating: Double?,
        minYear: Int?,
        maxYear: Int?,
        pageable: Pageable,
    ): GameFilterResponse

    /**
     * Find games by multiple tags (intersection - ALL tags must match).
     *
     * @param tagNames List of tag names
     * @param pageable Pagination and sorting
     * @return Games matching all specified tags
     */
    fun findGamesByAllTags(
        tagNames: List<String>,
        pageable: Pageable,
    ): GameFilterResponse
}

@Service
class GameFilterServiceImpl(
    private val gameRepository: GameRepository,
) : GameFilterService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun filterGames(
        tags: List<String>?,
        minRating: Double?,
        maxRating: Double?,
        minYear: Int?,
        maxYear: Int?,
        pageable: Pageable,
    ): GameFilterResponse {
        validateFilters(minRating, maxRating, minYear, maxYear)

        logger.debug(
            "Filtering games - tags: $tags, rating: [$minRating-$maxRating], " +
                "years: [$minYear-$maxYear], page: ${pageable.pageNumber}",
        )

        val spec = buildSpecification(tags, minRating, maxRating, minYear, maxYear)
        val page = gameRepository.findAll(spec, pageable)

        val games = page.content.map { GameSummaryDto.fromEntity(it) }

        logger.info("Filter completed: ${page.totalElements} games found")

        return GameFilterResponse(
            games = games,
            appliedFilters =
                AppliedFilters(
                    tags = tags,
                    minRating = minRating,
                    maxRating = maxRating,
                    minYear = minYear,
                    maxYear = maxYear,
                    platforms = null,
                ),
            page = page.number,
            pageSize = page.size,
            totalResults = page.totalElements,
            totalPages = page.totalPages,
        )
    }

    @Transactional(readOnly = true)
    override fun findGamesByAllTags(
        tagNames: List<String>,
        pageable: Pageable,
    ): GameFilterResponse {
        if (tagNames.isEmpty()) {
            throw InvalidFilterException("At least one tag must be specified")
        }

        logger.debug("Finding games with ALL tags: $tagNames")

        val normalizedNames = tagNames.map { it.lowercase().trim() }
        val games = gameRepository.findByAllTags(normalizedNames, normalizedNames.size.toLong())

        val paginatedGames =
            games
                .drop(pageable.pageNumber * pageable.pageSize)
                .take(pageable.pageSize)

        val totalPages = (games.size + pageable.pageSize - 1) / pageable.pageSize

        logger.info("Found ${games.size} games matching all tags: $tagNames")

        return GameFilterResponse(
            games = paginatedGames.map { GameSummaryDto.fromEntity(it) },
            appliedFilters =
                AppliedFilters(
                    tags = tagNames,
                    minRating = null,
                    maxRating = null,
                    minYear = null,
                    maxYear = null,
                    platforms = null,
                ),
            page = pageable.pageNumber,
            pageSize = pageable.pageSize,
            totalResults = games.size.toLong(),
            totalPages = totalPages,
        )
    }

    private fun buildSpecification(
        tags: List<String>?,
        minRating: Double?,
        maxRating: Double?,
        minYear: Int?,
        maxYear: Int?,
    ): Specification<Game> {
        var spec: Specification<Game> = Specification.unrestricted()

        tags?.forEach { tag ->
            val normalizedTag = tag.lowercase().trim()
            spec = spec.and(GameSpecifications.hasTag(normalizedTag))
        }

        minRating?.let {
            spec = spec.and(GameSpecifications.ratingGreaterThanOrEqual(it))
        }

        maxRating?.let {
            spec = spec.and(GameSpecifications.ratingLessThanOrEqual(it))
        }

        minYear?.let {
            spec = spec.and(GameSpecifications.releasedAfterOrIn(it))
        }

        maxYear?.let {
            spec = spec.and(GameSpecifications.releasedBeforeOrIn(it))
        }

        if (minRating != null || maxRating != null) {
            spec = spec.and(GameSpecifications.hasRating())
        }

        return spec
    }

    private fun validateFilters(
        minRating: Double?,
        maxRating: Double?,
        minYear: Int?,
        maxYear: Int?,
    ) {
        val errors = mutableListOf<String>()

        minRating?.let {
            if (it !in EtlConstants.ValidationConstants.MIN_RATING..EtlConstants.ValidationConstants.MAX_RATING) {
                errors.add(
                    "minRating must be between ${EtlConstants.ValidationConstants.MIN_RATING} " +
                        "and ${EtlConstants.ValidationConstants.MAX_RATING}",
                )
            }
        }

        maxRating?.let {
            if (it !in EtlConstants.ValidationConstants.MIN_RATING..EtlConstants.ValidationConstants.MAX_RATING) {
                errors.add(
                    "maxRating must be between ${EtlConstants.ValidationConstants.MIN_RATING} " +
                        "and ${EtlConstants.ValidationConstants.MAX_RATING}",
                )
            }
        }

        if (minRating != null && maxRating != null && minRating > maxRating) {
            errors.add("minRating cannot be greater than maxRating")
        }

        minYear?.let {
            if (it !in EtlConstants.ValidationConstants.MIN_YEAR..EtlConstants.ValidationConstants.MAX_YEAR) {
                errors.add(
                    "minYear must be between ${EtlConstants.ValidationConstants.MIN_YEAR} " +
                        "and ${EtlConstants.ValidationConstants.MAX_YEAR}",
                )
            }
        }

        maxYear?.let {
            if (it !in EtlConstants.ValidationConstants.MIN_YEAR..EtlConstants.ValidationConstants.MAX_YEAR) {
                errors.add(
                    "maxYear must be between ${EtlConstants.ValidationConstants.MIN_YEAR} " +
                        "and ${EtlConstants.ValidationConstants.MAX_YEAR}",
                )
            }
        }

        if (minYear != null && maxYear != null && minYear > maxYear) {
            errors.add("minYear cannot be greater than maxYear")
        }

        if (errors.isNotEmpty()) {
            throw InvalidFilterException(errors.joinToString("; "))
        }
    }
}

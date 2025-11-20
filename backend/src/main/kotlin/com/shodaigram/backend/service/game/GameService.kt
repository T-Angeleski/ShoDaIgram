package com.shodaigram.backend.service.game

import com.shodaigram.backend.domain.dto.game.GameDetailDto
import com.shodaigram.backend.domain.dto.game.GamePageDto
import com.shodaigram.backend.domain.dto.game.GameSummaryDto
import com.shodaigram.backend.domain.dto.game.TagDto
import com.shodaigram.backend.exception.GameNotFoundException
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.repository.GameTagRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for game retrieval operations
 */
interface GameService {
    fun getGameById(id: Long): GameDetailDto

    fun getGameBySlug(slug: String): GameDetailDto

    fun getAllGames(pageable: Pageable): GamePageDto
}

@Service
class GameServiceImpl(
    private val gameRepository: GameRepository,
    private val gameTagRepository: GameTagRepository,
) : GameService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    override fun getGameById(id: Long): GameDetailDto {
        logger.debug("Fetching game by ID: $id")

        val game =
            gameRepository.findById(id)
                .orElseThrow { GameNotFoundException("Game not found with ID: $id") }

        val gameTags = gameTagRepository.findByGameId(game.id!!)
        val tags = gameTags.map { TagDto.fromEntity(it.tag) }

        return GameDetailDto.fromEntity(game, tags)
    }

    @Transactional(readOnly = true)
    override fun getGameBySlug(slug: String): GameDetailDto {
        logger.debug("Fetching game by slug: $slug")

        val game =
            gameRepository.findBySlug(slug)
                ?: throw GameNotFoundException("Game not found with slug: $slug")

        val gameTags = gameTagRepository.findByGameId(game.id!!)
        val tags = gameTags.map { TagDto.fromEntity(it.tag) }

        return GameDetailDto.fromEntity(game, tags)
    }

    @Transactional(readOnly = true)
    override fun getAllGames(pageable: Pageable): GamePageDto {
        logger.debug("Fetching all games - page: ${pageable.pageNumber}, size: ${pageable.pageSize}")

        val page = gameRepository.findAll(pageable)

        return GamePageDto(
            games = page.content.map { GameSummaryDto.fromEntity(it) },
            page = page.number,
            pageSize = page.size,
            totalResults = page.totalElements,
            totalPages = page.totalPages,
            isFirst = page.isFirst,
            isLast = page.isLast,
        )
    }
}

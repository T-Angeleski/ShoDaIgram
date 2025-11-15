package com.shodaigram.backend.service.similarity

import com.shodaigram.backend.domain.dto.similarity.SimilarGameDto
import com.shodaigram.backend.domain.dto.similarity.SimilarityComputationResponse
import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.domain.entity.GameSimilarity
import com.shodaigram.backend.domain.entity.SimilarityType
import com.shodaigram.backend.exception.InvalidGameDataException
import com.shodaigram.backend.exception.LuceneIndexException
import com.shodaigram.backend.exception.SimilarityComputationException
import com.shodaigram.backend.repository.GameRepository
import com.shodaigram.backend.repository.GameSimilarityRepository
import com.shodaigram.backend.repository.GameTagRepository
import com.shodaigram.backend.util.LuceneIndexBuilder
import com.shodaigram.backend.util.SimilarityConstants
import com.shodaigram.backend.util.SimilarityConstants.BATCH_SIZE
import com.shodaigram.backend.util.SimilarityConstants.LOG_INTERVAL
import com.shodaigram.backend.util.TfIdfCalculator
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.ByteBuffersDirectory
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Service for computing TF-IDF based similarities between games.
 */
interface TfIdfSimilarityService {
    /**
     * Compute and store TF-IDF similarities for a specific game.
     *
     * @param gameId The ID of the game to compute similarities for.
     * @return A list of computed GameSimilarity entities.
     * @throws InvalidGameDataException if the game data is invalid.
     * @throws SimilarityComputationException if similarity computation fails.
     * @throws LuceneIndexException if there is an error with Lucene indexing.
     */
    fun computeSimilaritiesForGame(gameId: Long): List<GameSimilarity>

    /**
     * Compute and store TF-IDF similarities for all games in the database.
     *
     * @return A SimilarityComputationResponse summarizing the computation results.
     * @throws SimilarityComputationException if similarity computation fails.
     * @throws LuceneIndexException if there is an error with Lucene indexing.
     */
    fun computeAllSimilarities(): SimilarityComputationResponse

    /**
     * Retrieve similar games for a given game based on precomputed TF-IDF similarities.
     *
     * @param gameId The ID of the game to find similar games for.
     * @param limit The maximum number of similar games to return.
     * @return A list of SimilarGameDto representing similar games.
     */
    fun getSimilarGames(
        gameId: Long,
        limit: Int = 10,
    ): List<SimilarGameDto>
}

@Service
class TfIdfSimilarityServiceImpl(
    private val gameRepository: GameRepository,
    private val gameSimilarityRepository: GameSimilarityRepository,
    private val gameTagRepository: GameTagRepository,
    private val indexBuilder: LuceneIndexBuilder,
) : TfIdfSimilarityService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun computeSimilaritiesForGame(gameId: Long): List<GameSimilarity> {
        val startTime = System.currentTimeMillis()

        val game =
            gameRepository.findByIdOrNull(gameId)
                ?: throw InvalidGameDataException("Game not found with ID: $gameId")

        if (game.description.isNullOrBlank()) {
            throw InvalidGameDataException(
                "Game '${game.name}' has no description. Cannot compute TF-IDF similarities.",
            )
        }

        val allGames =
            gameRepository.findAll()
                .filter { !it.description.isNullOrBlank() }

        val (directory, gameVectors) = buildIndexAndExtractVectors(allGames)
        val similarities = computeSimilaritiesFromVectors(game, gameVectors, allGames)
        directory.close()

        gameSimilarityRepository.deletePrecomputedSimilaritiesByGameId(game.id!!)
        val savedSimilarities = gameSimilarityRepository.saveAll(similarities)

        val duration = System.currentTimeMillis() - startTime
        logger.info("Computed ${similarities.size} similarities for '${game.name}' in ${duration}ms")

        return savedSimilarities
    }

    @Transactional
    override fun computeAllSimilarities(): SimilarityComputationResponse {
        val startTime = System.currentTimeMillis()
        logger.info("Starting full TF-IDF similarity computation...")

        val allGames =
            gameRepository.findAll()
                .filter { !it.description.isNullOrBlank() }

        if (allGames.isEmpty()) {
            logger.warn("No games with descriptions found.")
            return SimilarityComputationResponse.emptyComputationResponse()
        }

        logger.info("Loaded ${allGames.size} games with descriptions")

        val (directory, gameVectors) = buildIndexAndExtractVectors(allGames)

        var totalSimilarities = 0
        var processedGames = 0
        val totalChunks = (allGames.size + BATCH_SIZE - 1) / BATCH_SIZE

        logger.info("Starting similarity computation for ${allGames.size} games in $totalChunks batches...")

        allGames.chunked(BATCH_SIZE).forEachIndexed { chunkIndex, chunk ->
            val allSimilaritiesInChunk = mutableListOf<GameSimilarity>()
            val successfulGameIds = mutableListOf<Long>()

            chunk.forEach { sourceGame ->
                try {
                    val similarities =
                        computeSimilaritiesFromVectors(
                            sourceGame,
                            gameVectors,
                            allGames,
                        )
                    allSimilaritiesInChunk.addAll(similarities)
                    successfulGameIds.add(sourceGame.id!!)
                    totalSimilarities += similarities.size
                    processedGames++

                    logProgressIfNeeded(processedGames, allGames.size, totalSimilarities)
                } catch (e: SimilarityComputationException) {
                    logger.error("Failed to compute similarities for '${sourceGame.name}'", e)
                } catch (e: LuceneIndexException) {
                    logger.error("Failed to compute similarities for '${sourceGame.name}'", e)
                } catch (e: InvalidGameDataException) {
                    logger.error("Failed to compute similarities for '${sourceGame.name}'", e)
                }
            }

            if (successfulGameIds.isNotEmpty()) {
                gameSimilarityRepository.deletePrecomputedSimilaritiesByGameIds(successfulGameIds)
                gameSimilarityRepository.saveAll(allSimilaritiesInChunk)
                logger.info(
                    "Batch ${chunkIndex + 1}/$totalChunks complete: " +
                        "Saved ${allSimilaritiesInChunk.size} similarities for ${successfulGameIds.size} games " +
                        "(Progress: $processedGames/${allGames.size} games, $totalSimilarities total similarities)",
                )
            }
        }

        directory.close()

        val duration = System.currentTimeMillis() - startTime
        logger.info(
            "Completed TF-IDF computation: $totalSimilarities similarities " +
                "for $processedGames games in ${duration}ms",
        )

        return SimilarityComputationResponse(
            status = "COMPLETED",
            gamesProcessed = processedGames,
            similaritiesComputed = totalSimilarities,
            durationMs = duration,
        )
    }

    @Transactional(readOnly = true)
    override fun getSimilarGames(
        gameId: Long,
        limit: Int,
    ): List<SimilarGameDto> {
        val similarities = gameSimilarityRepository.findTopSimilarGames(gameId, limit)

        return similarities.map { similarity ->
            val similarGame = similarity.similarGame

            SimilarGameDto(
                gameId = similarGame.id!!,
                name = similarGame.name,
                slug = similarGame.slug,
                rating = similarGame.rating,
                ratingCount = similarGame.ratingCount,
                releaseDate = similarGame.releaseDate?.toString(),
                backgroundImageUrl = similarGame.backgroundImageUrl,
                similarityScore = similarity.similarityScore,
                similarityType = SimilarityType.PRECOMPUTED_TF_IDF,
            )
        }
    }

    private fun buildIndexAndExtractVectors(
        allGames: List<Game>,
    ): Pair<ByteBuffersDirectory, Map<Long, Map<String, Float>>> {
        try {
            logger.info("Building Lucene index for ${allGames.size} games...")
            val directory = ByteBuffersDirectory()
            val analyzer = StandardAnalyzer()
            val config = IndexWriterConfig(analyzer)
            val writer = IndexWriter(directory, config)

            val gameIdToIndex = mutableMapOf<Long, Int>()
            var docIndex = 0

            allGames.forEachIndexed { index, game ->
                if (!game.description.isNullOrBlank()) {
                    val tags = gameTagRepository.findByGameId(game.id!!).toSet()
                    val doc = indexBuilder.buildGameDocument(game, tags)
                    writer.addDocument(doc)
                    gameIdToIndex[game.id!!] = docIndex
                    docIndex++

                    if ((index + 1) % LOG_INTERVAL == 0 || index == allGames.size - 1) {
                        logger.info("Indexed ${index + 1}/${allGames.size} games...")
                    }
                }
            }
            writer.close()
            logger.info("Lucene index built successfully. Extracting TF-IDF vectors...")

            val reader = DirectoryReader.open(directory)
            val gameVectors = mutableMapOf<Long, Map<String, Float>>()

            gameIdToIndex.entries.forEachIndexed { index, (gameId, docIdx) ->
                gameVectors[gameId] = buildGameVector(reader, docIdx)

                if ((index + 1) % LOG_INTERVAL == 0 || index == gameIdToIndex.size - 1) {
                    logger.info("Extracted vectors for ${index + 1}/${gameIdToIndex.size} games...")
                }
            }

            reader.close()
            logger.info("Vector extraction complete. Ready to compute similarities.")

            return Pair(directory, gameVectors)
        } catch (e: IOException) {
            throw LuceneIndexException("Failed to build Lucene index", e)
        } catch (e: RuntimeException) {
            throw LuceneIndexException("Failed to build Lucene index", e)
        }
    }

    private fun computeSimilaritiesFromVectors(
        sourceGame: Game,
        gameVectors: Map<Long, Map<String, Float>>,
        allGames: List<Game>,
    ): List<GameSimilarity> {
        try {
            val sourceVector = gameVectors[sourceGame.id!!] ?: return emptyList()

            if (sourceVector.isEmpty()) {
                logger.warn("Empty vector for game '${sourceGame.name}' (ID: ${sourceGame.id})")
                return emptyList()
            }

            val scoredGames =
                allGames
                    .filter { it.id != sourceGame.id && !it.description.isNullOrBlank() }
                    .mapNotNull { targetGame ->
                        val targetVector = gameVectors[targetGame.id!!] ?: return@mapNotNull null

                        val similarity = TfIdfCalculator.calculateCosineSimilarity(sourceVector, targetVector)

                        if (similarity >= SimilarityConstants.MIN_SIMILARITY_THRESHOLD) {
                            Pair(targetGame, similarity)
                        } else {
                            null
                        }
                    }
                    .sortedByDescending { it.second }
                    .take(SimilarityConstants.TOP_N_SIMILAR_GAMES)

            return scoredGames.map { (targetGame, similarity) ->
                GameSimilarity(
                    game = sourceGame,
                    similarGame = targetGame,
                    similarityScore =
                        BigDecimal(similarity).setScale(
                            SimilarityConstants.SIMILARITY_SCORE_SCALE,
                            RoundingMode.HALF_UP,
                        ),
                    similarityType = SimilarityType.PRECOMPUTED_TF_IDF,
                )
            }
        } catch (e: ArithmeticException) {
            throw SimilarityComputationException(
                "Failed to compute similarities from vectors",
                e,
            )
        } catch (e: RuntimeException) {
            throw SimilarityComputationException(
                "Failed to compute similarities from vectors",
                e,
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun buildGameVector(
        reader: DirectoryReader,
        docIndex: Int,
    ): Map<String, Float> {
        val combinedVector = mutableMapOf<String, Float>()
        val totalDocs = reader.numDocs()

        val fields =
            listOf(
                SimilarityConstants.LuceneFields.DESCRIPTION to SimilarityConstants.WEIGHT_DESCRIPTION,
                SimilarityConstants.LuceneFields.GENRE to SimilarityConstants.WEIGHT_GENRE,
                SimilarityConstants.LuceneFields.THEME to SimilarityConstants.WEIGHT_THEME,
                SimilarityConstants.LuceneFields.KEYWORD to SimilarityConstants.WEIGHT_KEYWORD,
            )

        fields.forEach { (fieldName, weight) ->
            val terms = reader.getTermVector(docIndex, fieldName)
            val fieldVector = TfIdfCalculator.buildTfIdfVector(terms, weight, totalDocs)

            fieldVector.forEach { (term, termWeight) ->
                combinedVector[term] = combinedVector.getOrDefault(term, 0f) + termWeight
            }
        }

        return TfIdfCalculator.normalizeVector(combinedVector)
    }

    private fun logProgressIfNeeded(
        processed: Int,
        total: Int,
        similarities: Int,
    ) {
        if (processed % SimilarityConstants.LOG_INTERVAL == 0) {
            logger.info("Processed $processed/$total games ($similarities similarities computed)")
        }
    }
}

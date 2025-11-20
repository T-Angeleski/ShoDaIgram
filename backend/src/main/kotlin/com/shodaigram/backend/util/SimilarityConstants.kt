package com.shodaigram.backend.util

import java.math.BigDecimal

/**
 * Constants for TF-IDF similarity computation and Lucene indexing.
 */
object SimilarityConstants {
    // Similarity thresholds and limits
    const val MIN_SIMILARITY_THRESHOLD = 0.1
    const val TOP_N_SIMILAR_GAMES = 20
    const val BATCH_SIZE = 100
    const val LOG_INTERVAL = 500

    // Field weights for TF-IDF computation
    // Higher weight = more important for similarity
    const val WEIGHT_DESCRIPTION = 2.0f
    const val WEIGHT_GENRE = 5.0f
    const val WEIGHT_THEME = 4.0f
    const val WEIGHT_KEYWORD = 2.5f

    // Database similarity score precision
    const val SIMILARITY_SCORE_SCALE = 4

    /**
     * Explainability weights for MatchReason contributions.
     *
     * These weights control how we present "why" a game was recommended.
     * They do NOT affect the actual similarity ranking (which uses TF-IDF).
     *
     * Rationale:
     * - GENRE: Highest signal - users strongly expect shared genres in recommendations
     * - THEME: High signal - captures mood/narrative elements (e.g., dark fantasy, horror)
     * - FRANCHISE: Moderate signal - meaningful but only applicable to games in same franchise
     * - DESCRIPTION_FACTOR: Scales the TF-IDF similarity score when shown as a reason
     *
     * Note: These don't need to sum to 1.0 since not all categories appear for every game pair.
     */
    object ExplainabilityWeights {
        val GENRE_CONTRIBUTION = BigDecimal("0.30")
        val THEME_CONTRIBUTION = BigDecimal("0.25")
        val FRANCHISE_CONTRIBUTION = BigDecimal("0.20")
        val DESCRIPTION_FACTOR = BigDecimal("0.25")
    }

    /**
     * Constants for date and time operations.
     */
    object DateConstants {
        const val FIRST_MONTH = 1
        const val LAST_MONTH = 12
        const val FIRST_DAY = 1
        const val LAST_DAY = 31
    }

    /**
     * Lucene document field names.
     */
    object LuceneFields {
        const val GAME_ID = "gameId"
        const val DESCRIPTION = "description"
        const val GENRE = "genre"
        const val THEME = "theme"
        const val KEYWORD = "keyword"
    }

    /**
     * Tag categories that contribute to similarity.
     */
    object TagCategories {
        const val GENRE = "GENRE"
        const val THEME = "THEME"
        const val KEYWORD = "KEYWORD"
    }
}

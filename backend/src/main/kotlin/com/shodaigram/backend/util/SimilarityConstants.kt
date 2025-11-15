package com.shodaigram.backend.util

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

package com.shodaigram.backend.util

import java.math.BigDecimal

/**
 * Constants for ETL pipeline configuration.
 * These values balance performance, memory usage, and data quality.
 */
object EtlConstants {
    /**
     * Number of records to process per database transaction.
     * 1000 provides good balance between memory usage and transaction overhead.
     * Larger batches = more memory, faster processing but harder rollback.
     * Smaller batches = more transactions, slower but better error isolation.
     */
    const val BATCH_SIZE = 1000

    /**
     * Log progress every N records to monitor ETL without spam.
     * 500 provides visibility into progress without flooding logs.
     */
    const val LOG_INTERVAL = 500

    /**
     * Maximum number of similar games to store per game.
     * IGDB API provides up to 10 similar games per game.
     * Limiting prevents storage bloat while retaining most relevant similarities.
     */
    const val MAX_SIMILAR_GAMES = 10

    /**
     * Maximum Levenshtein edit distance for fuzzy name matching.
     * Distance of 2 means "very similar" (e.g., "Skyrim" vs "Skyrm").
     * Higher values increase false positives; lower values miss valid matches.
     */
    const val MAX_LEVENSHTEIN_DISTANCE = 2

    /**
     * Minimum game name length to perform Levenshtein comparison.
     * Short names (< 3 chars) produce too many false positives in fuzzy matching.
     * Example: "FF" could match "FF7", "FF14", "FIFA", etc.
     */
    const val MIN_NAME_LENGTH = 3

    /**
     * Default similarity score for IGDB API-provided similar_games relationships.
     * 0.80 indicates high confidence (IGDB's editorial curation is trustworthy).
     * Score range: 0.0 (unrelated) to 1.0 (identical/clones).
     */
    val DEFAULT_SIMILARITY_SCORE = BigDecimal("0.8000")
}

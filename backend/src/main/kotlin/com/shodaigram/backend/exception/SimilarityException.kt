package com.shodaigram.backend.exception

sealed class SimilarityException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Thrown when Apache Lucene indexing operations fail.
 */
class LuceneIndexException(
    message: String,
    cause: Throwable? = null,
) : SimilarityException(message, cause)

/**
 * Thrown when TF-IDF similarity computation fails.
 */
class SimilarityComputationException(
    message: String,
    cause: Throwable? = null,
) : SimilarityException(message, cause)

/**
 * Thrown when attempting to compute similarities for a game with invalid/missing data.
 */
class InvalidGameDataException(
    message: String,
) : SimilarityException(message)

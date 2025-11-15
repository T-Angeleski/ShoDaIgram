package com.shodaigram.backend.util

import com.shodaigram.backend.exception.SimilarityComputationException
import org.apache.lucene.index.Terms
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Utility object for TF-IDF vector operations and cosine similarity calculations.
 */
object TfIdfCalculator {
    /**
     * Calculates cosine similarity between two TF-IDF vectors.
     *
     * Formula: cos(θ) = (A · B) / (||A|| × ||B||)
     *
     * @param vector1 First TF-IDF vector (term -> weight)
     * @param vector2 Second TF-IDF vector (term -> weight)
     * @return Similarity score between 0.0 and 1.0
     */
    fun calculateCosineSimilarity(
        vector1: Map<String, Float>,
        vector2: Map<String, Float>,
    ): Double {
        if (vector1.isEmpty() || vector2.isEmpty()) return 0.0

        try {
            val dotProduct = calculateDotProduct(vector1, vector2)
            val magnitude1 = calculateMagnitude(vector1)
            val magnitude2 = calculateMagnitude(vector2)

            if (magnitude1 == 0.0 || magnitude2 == 0.0) return 0.0

            return dotProduct / (magnitude1 * magnitude2)
        } catch (e: Exception) {
            throw SimilarityComputationException(
                "Failed to calculate cosine similarity",
                e,
            )
        }
    }

    /**
     * Builds a TF-IDF vector from Lucene Terms with field weighting.
     *
     * Uses TF-IDF formula:
     * TF = term frequency in document
     * IDF = log(total_docs / doc_frequency)
     *
     * @param terms Lucene Terms object from a document field
     * @param fieldWeight Weight multiplier for this field (e.g., 5.0 for description)
     * @param totalDocs Total number of documents in the index
     * @return Map of term -> weighted TF-IDF score
     */
    fun buildTfIdfVector(
        terms: Terms?,
        fieldWeight: Float,
        totalDocs: Int,
    ): Map<String, Float> {
        if (terms == null) return emptyMap()

        val vector = mutableMapOf<String, Float>()

        try {
            val termsEnum = terms.iterator()
            var term = termsEnum.next()

            while (term != null) {
                val termText = term.utf8ToString()
                val termFreq = termsEnum.totalTermFreq().toFloat()
                val docFreq = termsEnum.docFreq().toFloat()

                // TF-IDF = TF × IDF × field_weight
                val tf = termFreq
                val idf = ln(totalDocs.toFloat() / docFreq)
                val tfidf = tf * idf * fieldWeight

                vector[termText] = tfidf

                term = termsEnum.next()
            }
        } catch (e: Exception) {
            throw SimilarityComputationException(
                "Failed to build TF-IDf vector from terms",
                e,
            )
        }

        return vector
    }

    /**
     * Normalizes a TF-IDF vector to unit length.
     *
     * @param vector Original vector
     * @return Normalized vector
     */
    fun normalizeVector(vector: Map<String, Float>): Map<String, Float> {
        val magnitude = calculateMagnitude(vector)
        if (magnitude == 0.0) return vector

        return vector.mapValues { (_, value) ->
            (value / magnitude).toFloat()
        }
    }

    /**
     * Calculates dot product of two vectors.
     * Only considers terms present in both vectors.
     */
    private fun calculateDotProduct(
        vector1: Map<String, Float>,
        vector2: Map<String, Float>,
    ): Double {
        var dotProduct = 0.0
        for ((term, weight1) in vector1) {
            val weight2 = vector2[term]
            if (weight2 != null) {
                dotProduct += weight1 * weight2
            }
        }
        return dotProduct
    }

    /**
     * Calculates magnitude (L2 norm) of a vector.
     */
    private fun calculateMagnitude(vector: Map<String, Float>): Double {
        val sumOfSquares = vector.values.sumOf { (it * it).toDouble() }
        return sqrt(sumOfSquares)
    }
}

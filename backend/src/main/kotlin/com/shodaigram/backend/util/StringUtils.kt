package com.shodaigram.backend.util

import org.springframework.data.domain.Sort

/**
 * String utilities for ETL operations - fuzzy matching, normalization, slug generation.
 */
object StringUtils {
    /**
     * Computes Levenshtein distance between two strings (case-insensitive).
     * Used for fuzzy duplicate detection.
     *
     *
     * @return Edit distance (0 = identical, higher = more different)
     */
    fun levenshteinDistance(
        s1: String,
        s2: String,
    ): Int {
        val str1 = s1.lowercase()
        val str2 = s2.lowercase()

        val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }

        for (i in 0..str1.length) dp[i][0] = i
        for (j in 0..str2.length) dp[0][j] = j

        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                dp[i][j] =
                    minOf(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + cost,
                    )
            }
        }

        return dp[str1.length][str2.length]
    }

    /**
     * Normalizes a string for comparison/indexing:
     * - Lowercase
     * - Replace spaces/underscores with hyphens
     * - Remove special characters except hyphens
     * - Trim whitespace
     *
     * Examples:
     * - "First Person" → "first-person"
     * - "Sci-Fi & Fantasy" → "sci-fi-fantasy"
     * - "Open World" → "open-world"
     */
    fun normalize(input: String): String {
        return input
            .lowercase()
            .trim()
            .replace(Regex("[\\s_]+"), "-") // Spaces/underscores → hyphens
            .replace(Regex("[^a-z0-9-]"), "") // Remove non-alphanumeric except hyphens
            .replace(Regex("-+"), "-") // Collapse multiple hyphens
            .trim('-') // Remove leading/trailing hyphens
    }

    /**
     * Parses sort parameter into Spring Data Sort object.
     */
    fun parseSortParam(sortParam: String): Sort {
        val parts = sortParam.split(",")
        val field = parts.getOrNull(0) ?: "rating"
        val direction = parts.getOrNull(1)?.uppercase() ?: "DESC"

        return if (direction == "ASC") {
            Sort.by(Sort.Order.asc(field))
        } else {
            Sort.by(Sort.Order.desc(field))
        }
    }
}

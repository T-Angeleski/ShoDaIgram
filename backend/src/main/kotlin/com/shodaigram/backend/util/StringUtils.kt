package com.shodaigram.backend.util

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
     * Generates URL-safe slug from game name.
     * If input already looks like a slug, returns normalized version.
     *
     * Examples:
     * - "The Elder Scrolls VI" → "the-elder-scrolls-vi"
     * - "Grand Theft Auto V" → "grand-theft-auto-v"
     */
    fun toSlug(input: String): String = normalize(input)

    /**
     * Fuzzy string matching using normalized Levenshtein distance.
     *
     * @param threshold Max edit distance to consider a match (default: 2)
     * @return true if strings are similar within threshold
     */
    fun fuzzyMatch(
        s1: String,
        s2: String,
        threshold: Int = 2,
    ): Boolean {
        val normalized1 = normalize(s1)
        val normalized2 = normalize(s2)
        return levenshteinDistance(normalized1, normalized2) <= threshold
    }

    /**
     * Calculates similarity ratio (0.0 - 1.0) using Levenshtein distance.
     * 1.0 = identical, 0.0 = completely different
     */
    fun similarityRatio(
        s1: String,
        s2: String,
    ): Double {
        val maxLen = maxOf(s1.length, s2.length)
        if (maxLen == 0) return 1.0
        val distance = levenshteinDistance(s1, s2)
        return 1.0 - (distance.toDouble() / maxLen)
    }
}

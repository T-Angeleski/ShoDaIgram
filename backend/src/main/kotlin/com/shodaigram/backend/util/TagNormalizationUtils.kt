package com.shodaigram.backend.util

import com.shodaigram.backend.domain.entity.TagCategory
import java.math.BigDecimal

/**
 * Utility object for tag normalization logic.
 */
object TagNormalizationUtils {
    /**
     * Manual synonym map for tag normalization.
     * Maps common variations to canonical forms.
     */
    val SYNONYM_MAP =
        mapOf(
            "sci-fi" to "sci-fi",
            "science fiction" to "sci-fi",
            "fps" to "first-person-shooter",
            "first-person shooter" to "first-person-shooter",
            "rpg" to "role-playing-game",
            "role-playing" to "role-playing-game",
            "action-adventure" to "action-adventure",
            "open world" to "open-world",
        )

    /**
     * Category-based weights for tag importance in recommendations.
     * Higher weights = stronger influence on similarity scores.
     *
     * Weight rationale:
     * - **GENRE (1.00)**: Strongest signal for game similarity. Users searching for "RPG" expect RPG results.
     * - **THEME (0.80)**: High importance. "Sci-Fi" or "Horror" themes strongly indicate similar experiences.
     * - **KEYWORD (0.67)**: Medium-high. User-generated tags can be insightful but variable in quality.
     * - **PLAYER_PERSPECTIVE (0.60)**: Moderately important. First-person vs third-person affects gameplay feel.
     * - **PLATFORM (0.53)**: Medium. Platform overlap matters but doesn't define similarity (many games are multiplatform).
     * - **FRANCHISE (0.47)**: Medium-low. Same franchise doesn't guarantee similar gameplay (e.g., spin-offs).
     * - **DEVELOPER/PUBLISHER (0.40)**: Lower weight. Same studio doesn't always mean similar games (different genres, IPs).
     *
     * These weights are tuned for balancing content-based and collaborative filtering in the hybrid recommendation engine.
     */
    val CATEGORY_WEIGHTS =
        mapOf(
            TagCategory.GENRE to BigDecimal("1.00"),
            TagCategory.THEME to BigDecimal("0.80"),
            TagCategory.GAME_MODE to BigDecimal("0.67"),
            TagCategory.PLATFORM to BigDecimal("0.53"),
            TagCategory.DEVELOPER to BigDecimal("0.40"),
            TagCategory.PUBLISHER to BigDecimal("0.40"),
            TagCategory.KEYWORD to BigDecimal("0.67"),
            TagCategory.FRANCHISE to BigDecimal("0.47"),
            TagCategory.PLAYER_PERSPECTIVE to BigDecimal("0.60"),
        )

    /**
     * Normalize tag name:
     * - Lowercase
     * - Trim whitespace
     * - Replace spaces with hyphens
     * - Apply synonym map
     *
     * @param name Raw tag name
     * @return Normalized tag name
     */
    fun normalizeTagName(name: String): String {
        require(name.isNotBlank()) { "Tag name cannot be blank" }

        val normalized =
            name.lowercase()
                .trim()
                .replace(Regex("\\s+"), "-")

        return SYNONYM_MAP[normalized] ?: normalized
    }

    /**
     * Get weight for a given tag category.
     *
     * @param category Tag category
     * @return Weight as BigDecimal, defaults to 1.0 if category not found
     */
    fun getWeightForCategory(category: TagCategory): BigDecimal = CATEGORY_WEIGHTS[category] ?: BigDecimal.ONE
}

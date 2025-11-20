package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.domain.entity.GameTag
import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.util.SimilarityConstants
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.time.LocalDate

/**
 * JPA Specifications for dynamic game filtering.
 * Provides type-safe, composable query building for complex filter combinations.
 */
object GameSpecifications {
    /**
     * Filter games that have a specific tag (by normalized name).
     */
    fun hasTag(tagNormalizedName: String): Specification<Game> =
        Specification { root, _, criteriaBuilder ->
            val gameTagJoin: Join<Game, GameTag> = root.join("gameTags", JoinType.INNER)
            val tagJoin: Join<GameTag, Tag> = gameTagJoin.join("tag", JoinType.INNER)

            criteriaBuilder.equal(
                criteriaBuilder.lower(tagJoin.get("normalizedName")),
                tagNormalizedName.lowercase(),
            )
        }

    /**
     * Filter games with rating greater than or equal to minimum.
     */
    fun ratingGreaterThanOrEqual(minRating: Double): Specification<Game> =
        Specification { root, _, criteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(
                root.get("rating"),
                BigDecimal.valueOf(minRating),
            )
        }

    /**
     * Filter games with rating less than or equal to maximum.
     */
    fun ratingLessThanOrEqual(maxRating: Double): Specification<Game> =
        Specification { root, _, criteriaBuilder ->
            criteriaBuilder.lessThanOrEqualTo(
                root.get("rating"),
                BigDecimal.valueOf(maxRating),
            )
        }

    /**
     * Filter games released on or after a specific year.
     */
    fun releasedAfterOrIn(year: Int): Specification<Game> =
        Specification { root, _, criteriaBuilder ->
            val startDate =
                LocalDate.of(
                    year,
                    SimilarityConstants.DateConstants.FIRST_MONTH,
                    SimilarityConstants.DateConstants.FIRST_DAY,
                )
            criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), startDate)
        }

    /**
     * Filter games released on or before a specific year.
     */
    fun releasedBeforeOrIn(year: Int): Specification<Game> =
        Specification { root, _, criteriaBuilder ->
            val endDate =
                LocalDate.of(
                    year,
                    SimilarityConstants.DateConstants.LAST_MONTH,
                    SimilarityConstants.DateConstants.LAST_DAY,
                )
            criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), endDate)
        }

    /**
     * Filter games with rating not null (excludes unrated games).
     */
    fun hasRating(): Specification<Game> =
        Specification { root, _, criteriaBuilder ->
            criteriaBuilder.isNotNull(root.get<BigDecimal>("rating"))
        }
}

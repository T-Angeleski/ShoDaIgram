package com.shodaigram.backend.domain.dto.etl

import java.time.LocalDate

/**
 * Intermediate representation combining RAWG + IGDB data before entity persistence.
 * Used for duplicate detection and merging logic.
 */
data class MergedGameDto(
    // Identifiers
    val igdbId: Long?,
    val rawgId: Long?,
    // Core metadata
    val name: String,
    val slug: String,
    val description: String?,
    val releaseDate: LocalDate?,
    // Ratings (averaged if both exist)
    val rating: Double?,
    val ratingCount: Int?,
    // Images (IGDB cover URL preferred)
    val backgroundImageUrl: String?,
    val coverUrl: String?,
    // Gameplay metadata
    val playtime: Int?,
    val metacritic: Int?,
    // Categorization
    val genres: Set<String>,
    val platforms: Set<String>,
    val tags: Set<String>,
    val developers: Set<String>,
    val publishers: Set<String>,
    // Source tracking
    val dataSources: Set<String>,
    // Merge metadata
    val mergeStrategy: MergeStrategy = MergeStrategy.SINGLE_SOURCE,
) {
    enum class MergeStrategy {
        SINGLE_SOURCE, // Only one source had this game
        EXACT_SLUG_MATCH, // Slugs matched exactly
        FUZZY_NAME_MATCH, // Names matched via Levenshtein
        MANUAL_REVIEW, // Ambiguous - needs review
    }

    /**
     * Combines two games into a merged representation.
     * IGDB takes priority for core metadata.
     * Note: Ratings should already be normalized to 0-10 scale in DTOs before merging
     */
    companion object {
        fun merge(
            rawg: RawgGameDto?,
            igdb: IgdbGameDto?,
        ): MergedGameDto {
            require(rawg != null || igdb != null) { "At least one source required" }

            return MergedGameDto(
                igdbId = igdb?.igdbId,
                rawgId = rawg?.rawgId,
                name = igdb?.name ?: rawg!!.name,
                slug = igdb?.slug ?: rawg!!.slug,
                description = selectDescription(igdb, rawg),
                releaseDate = igdb?.firstReleaseDate ?: rawg?.released,
                rating = averageRatings(igdb?.rating, rawg?.rating),
                ratingCount = sumRatingCounts(igdb?.ratingCount, rawg?.ratingsCount),
                backgroundImageUrl = selectBackgroundImage(igdb, rawg),
                coverUrl = igdb?.coverUrl,
                playtime = rawg?.playtime,
                metacritic = rawg?.metacritic,
                genres = mergeCollections(igdb?.genres, rawg?.genres),
                platforms = mergeCollections(igdb?.platforms, rawg?.platforms),
                tags = buildTagSet(igdb, rawg),
                developers = mergeCollections(igdb?.developers, rawg?.developers),
                publishers = mergeCollections(igdb?.publishers, rawg?.publishers),
                dataSources = buildDataSources(rawg, igdb),
                mergeStrategy = getMergeStrategy(rawg, igdb),
            )
        }

        private fun selectDescription(
            igdb: IgdbGameDto?,
            rawg: RawgGameDto?,
        ): String? = igdb?.summary ?: igdb?.storyline ?: rawg?.descriptionRaw

        private fun sumRatingCounts(
            igdbCount: Int?,
            rawgCount: Int?,
        ): Int = (igdbCount ?: 0) + (rawgCount ?: 0)

        private fun selectBackgroundImage(
            igdb: IgdbGameDto?,
            rawg: RawgGameDto?,
        ): String? = igdb?.coverUrl ?: rawg?.backgroundImage

        private fun mergeCollections(
            igdbList: List<String>?,
            rawgList: List<String>?,
        ): Set<String> = (igdbList ?: emptyList()).union(rawgList ?: emptyList())

        /**
         * Average ratings from both sources.
         * Both RAWG (0-5 → 0-10) and IGDB (0-100 → 0-10) are normalized before this point.
         */
        private fun averageRatings(
            igdbRating: Double?,
            rawgRating: Double?,
        ): Double? {
            val ratings = listOfNotNull(igdbRating, rawgRating)
            return if (ratings.isEmpty()) null else ratings.average()
        }

        private fun buildTagSet(
            igdb: IgdbGameDto?,
            rawg: RawgGameDto?,
        ): Set<String> {
            val tags = mutableSetOf<String>()
            igdb?.let {
                tags.addAll(it.themes)
                tags.addAll(it.gameModes)
                tags.addAll(it.keywords)
                tags.addAll(it.playerPerspectives)
            }
            rawg?.let { tags.addAll(it.tags) }
            return tags
        }

        /**
         * Build data sources set from available DTOs.
         */
        private fun buildDataSources(
            rawg: RawgGameDto?,
            igdb: IgdbGameDto?,
        ): Set<String> {
            val sources = mutableSetOf<String>()
            if (rawg != null) sources.add("rawg")
            if (igdb != null) sources.add(igdb.dataSource)
            return sources
        }

        private fun getMergeStrategy(
            rawg: RawgGameDto?,
            igdb: IgdbGameDto?,
        ): MergeStrategy {
            return when {
                rawg == null || igdb == null -> MergeStrategy.SINGLE_SOURCE
                rawg.slug.equals(igdb.slug, ignoreCase = true) -> MergeStrategy.EXACT_SLUG_MATCH
                else -> MergeStrategy.FUZZY_NAME_MATCH // Will be determined in merge service
            }
        }
    }
}

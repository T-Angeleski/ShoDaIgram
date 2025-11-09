package com.shodaigram.backend.service.etl

import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.domain.entity.GameTag
import com.shodaigram.backend.domain.entity.Tag
import com.shodaigram.backend.domain.entity.TagCategory
import com.shodaigram.backend.repository.GameTagRepository
import com.shodaigram.backend.repository.TagRepository
import com.shodaigram.backend.util.TagNormalizationUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for extracting and associating tags with games during ETL.
 * Handles tag creation, deduplication, normalization, and weight assignment.
 */
interface TagExtractionService {
    /**
     * Extract and associate tags with a game.
     *
     * @param game The game to associate tags with
     * @param tagsByCategory Map of tag names grouped by category
     */
    fun extractAndAssociateTags(
        game: Game,
        tagsByCategory: Map<TagCategory, List<String>>,
    )
}

@Service
class TagExtractionServiceImpl(
    private val tagRepository: TagRepository,
    private val gameTagRepository: GameTagRepository,
) : TagExtractionService {
    @Transactional
    override fun extractAndAssociateTags(
        game: Game,
        tagsByCategory: Map<TagCategory, List<String>>,
    ) {
        // Collect unique tags (deduplicate within this game)
        val uniqueTags = mutableSetOf<Pair<Tag, TagCategory>>()

        tagsByCategory.forEach { (category, tagNames) ->
            tagNames.forEach { tagName ->
                if (tagName.isNotBlank()) {
                    val tag = findOrCreateTag(tagName, category)
                    uniqueTags.add(tag to category)
                }
            }
        }

        val existingTagIds = gameTagRepository.findTagIdsByGameId(game.id!!)
        val uniqueTagsWithIds =
            uniqueTags.map { (tag, category) ->
                val tagId = requireNotNull(tag.id) { "Tag ID should not be null after save" }
                Triple(tag, category, tagId)
            }

        val newGameTags =
            uniqueTagsWithIds
                .filter { (_, _, tagId) -> tagId !in existingTagIds }
                .map { (tag, category, _) ->
                    val weight = TagNormalizationUtils.getWeightForCategory(category)
                    GameTag(
                        game = game,
                        tag = tag,
                        weight = weight,
                    )
                }

        if (newGameTags.isNotEmpty()) {
            gameTagRepository.saveAll(newGameTags)
        }
    }

    /**
     * Find existing tag or create new one.
     * Uses normalized name to prevent duplicates.
     */
    private fun findOrCreateTag(
        name: String,
        category: TagCategory,
    ): Tag {
        val normalizedName = TagNormalizationUtils.normalizeTagName(name)

        return tagRepository.findByNormalizedNameAndCategory(normalizedName, category)
            ?: tagRepository.save(
                Tag(
                    name = name,
                    normalizedName = normalizedName,
                    category = category,
                ),
            )
    }
}

package com.shodaigram.backend.util

import com.shodaigram.backend.domain.entity.Game
import com.shodaigram.backend.domain.entity.GameTag
import com.shodaigram.backend.exception.LuceneIndexException
import com.shodaigram.backend.util.SimilarityConstants.LuceneFields.DESCRIPTION
import com.shodaigram.backend.util.SimilarityConstants.LuceneFields.GAME_ID
import com.shodaigram.backend.util.SimilarityConstants.LuceneFields.GENRE
import com.shodaigram.backend.util.SimilarityConstants.LuceneFields.KEYWORD
import com.shodaigram.backend.util.SimilarityConstants.LuceneFields.THEME
import com.shodaigram.backend.util.SimilarityConstants.TagCategories
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.StoredField
import org.apache.lucene.index.IndexOptions
import org.springframework.stereotype.Component

/**
 * Utility for building Lucene documents from game entities.
 */
@Component
class LuceneIndexBuilder {
    /**
     * Builds a Lucene document from a game and its tags
     *
     * Document structure:
     * - gameId (stored, not indexed)
     * - description (indexed with high weight)
     * - genre tags (indexed with medium weight)
     * - theme tags (indexed with medium weight)
     * - keyword tags (indexed with lower weight)
     *
     * @param game Game entity
     * @param tags Associated tags for the game
     * @return Lucene Document ready for indexing
     */
    fun buildGameDocument(
        game: Game,
        tags: Set<GameTag>,
    ): Document {
        try {
            val doc = Document()

            doc.add(StoredField(GAME_ID, game.id!!))

            game.description?.let { description ->
                addWeightedTextField(
                    doc,
                    DESCRIPTION,
                    description,
                )
            }

            val tagsByCategory = tags.groupBy { it.tag.category.name }

            val genreTags = tagsByCategory[TagCategories.GENRE]?.joinToString(" ") { it.tag.name }
            if (!genreTags.isNullOrBlank()) {
                addWeightedTextField(
                    doc,
                    GENRE,
                    genreTags,
                )
            }

            val themeTags = tagsByCategory[TagCategories.THEME]?.joinToString(" ") { it.tag.name }
            if (!themeTags.isNullOrBlank()) {
                addWeightedTextField(
                    doc,
                    THEME,
                    themeTags,
                )
            }

            val keywordTags = tagsByCategory[TagCategories.KEYWORD]?.joinToString(" ") { it.tag.name }
            if (!keywordTags.isNullOrBlank()) {
                addWeightedTextField(
                    doc,
                    KEYWORD,
                    keywordTags,
                )
            }

            return doc
        } catch (e: Exception) {
            throw LuceneIndexException(
                "Failed to build Lucene document for game: ${game.slug}",
                e,
            )
        }
    }

    private fun addWeightedTextField(
        doc: Document,
        fieldName: String,
        content: String,
    ) {
        val fieldType =
            FieldType().apply {
                setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
                setTokenized(true)
                setStored(false)
                setStoreTermVectors(true)
                setStoreTermVectorPositions(true)
                freeze()
            }
        doc.add(Field(fieldName, content, fieldType))
    }
}

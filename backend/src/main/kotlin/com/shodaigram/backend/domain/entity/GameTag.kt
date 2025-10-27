package com.shodaigram.backend.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "game_tags")
data class GameTag(
    @EmbeddedId
    val id: GameTagId = GameTagId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    val game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    val tag: Tag,

    @Column(name = "weight", precision = 3, scale = 2)
    val weight: BigDecimal = BigDecimal.ONE,

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant? = null
)

@Embeddable
data class GameTagId(
    @Column(name = "game_id")
    val gameId: Long = 0,

    @Column(name = "tag_id")
    val tagId: Long = 0,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameTagId) return false
        return gameId == other.gameId && tagId == other.tagId
    }
}

//Searchvector handling?
//will i need lifecycle hooks for createdAt/updatedAt, do I really need to override the equals,hashcode,tostring
//what and why is this Embeddable thing with the gametag

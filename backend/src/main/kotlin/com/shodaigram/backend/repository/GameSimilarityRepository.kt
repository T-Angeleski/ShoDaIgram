package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.GameSimilarity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameSimilarityRepository : JpaRepository<GameSimilarity, Long> {
}

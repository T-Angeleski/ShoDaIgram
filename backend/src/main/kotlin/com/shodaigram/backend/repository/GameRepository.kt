package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long>

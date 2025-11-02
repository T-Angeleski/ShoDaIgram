package com.shodaigram.backend.util

import java.math.BigDecimal

object EtlConstants {
    const val BATCH_SIZE = 1000
    const val LOG_INTERVAL = 500
    const val MAX_SIMILAR_GAMES = 10
    val DEFAULT_SIMILARITY_SCORE = BigDecimal("0.8000")
}

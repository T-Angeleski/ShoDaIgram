package com.shodaigram.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration properties for ETL pipeline.
 * File paths can be overridden via environment variables or application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "etl")
data class EtlProperties(
    var dataFiles: DataFiles = DataFiles(),
) {
    data class DataFiles(
        var rawgPath: String = "src/main/data/rawg_games.json",
        var igdbPath: String = "src/main/data/igdb_games.json",
    )
}

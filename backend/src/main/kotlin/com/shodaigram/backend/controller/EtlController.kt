package com.shodaigram.backend.controller

import com.shodaigram.backend.config.EtlProperties
import com.shodaigram.backend.domain.dto.etl.EtlReport
import com.shodaigram.backend.domain.dto.etl.EtlReportDto
import com.shodaigram.backend.service.etl.EtlOrchestratorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/etl")
@Tag(name = "ETL", description = "Game data ETL pipeline management")
class EtlController(
    private val etlOrchestratorService: EtlOrchestratorService,
    private val etlProperties: EtlProperties,
) {
    @PostMapping("/run")
    @Operation(
        summary = "Run full ETL pipeline",
        description = """
            Execute complete ETL process:
            1. Import RAWG games
            2. Import IGDB games + similar_games
            3. Process IGDB similar_games references
            4. Merge duplicates

            File paths are configured in application.yml or via environment variables:
            - ETL_RAWG_FILE_PATH
            - ETL_IGDB_FILE_PATH
        """,
    )
    fun runEtl(): ResponseEntity<EtlReportDto> {
        val report: EtlReport =
            etlOrchestratorService.runEtl(
                rawgFilePath = etlProperties.dataFiles.rawgPath,
                igdbFilePath = etlProperties.dataFiles.igdbPath,
            )
        return ResponseEntity.ok(report.toDto())
    }
}

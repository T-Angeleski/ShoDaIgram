package com.shodaigram.backend.controller

import com.shodaigram.backend.domain.dto.etl.EtlReport
import com.shodaigram.backend.domain.dto.etl.EtlReportDto
import com.shodaigram.backend.service.etl.EtlOrchestratorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/etl")
@Tag(name = "ETL", description = "Game data ETL pipeline management")
class EtlController(
    private val etlOrchestratorService: EtlOrchestratorService,
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
            5. Normalize tags
        """,
    )
    fun runEtl(
        @RequestBody request: EtlRunRequest,
    ): ResponseEntity<EtlReportDto> {
        val report: EtlReport = etlOrchestratorService.runEtl(request.rawgFilePath, request.igdbFilePath)
        return ResponseEntity.ok(report.toDto())
    }
}

data class EtlRunRequest(
    @field:NotBlank(message = "RAWG file path is required")
    val rawgFilePath: String,
    @field:NotBlank(message = "IGDB file path is required")
    val igdbFilePath: String,
)

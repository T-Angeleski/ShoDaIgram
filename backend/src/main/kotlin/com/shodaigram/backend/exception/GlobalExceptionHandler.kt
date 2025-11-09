package com.shodaigram.backend.exception

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.net.URI
import java.time.Instant

/**
 * Global exception handler using RFC 7807 Problem Details.
 * Provides consistent error responses across all API endpoints.
 *
 * Error types (for client-side handling):
 * - `/problems/not-found` - Resource does not exist
 * - `/problems/validation-error` - Request validation failed
 * - `/problems/etl-error` - ETL processing failed
 * - `/problems/internal-error` - Unexpected server error
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(
        ex: EntityNotFoundException,
        request: WebRequest,
    ): ProblemDetail {
        val problem =
            ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.message ?: "Entity not found",
            )
        problem.title = "Resource Not Found"
        problem.type = URI.create("/problems/not-found") // Machine-readable error type
        problem.setProperty("timestamp", Instant.now())
        problem.setProperty("path", extractPath(request))
        return problem
    }

    @ExceptionHandler(EtlException::class)
    fun handleEtlException(
        ex: EtlException,
        request: WebRequest,
    ): ProblemDetail {
        val problem =
            ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.message ?: "ETL processing failed",
            )
        problem.title = "ETL Processing Error"
        problem.type = URI.create("/problems/etl-error")
        problem.setProperty("timestamp", Instant.now())
        problem.setProperty("path", extractPath(request))
        return problem
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ProblemDetail {
        val errors =
            ex.bindingResult.fieldErrors
                .associate { it.field to (it.defaultMessage ?: "Invalid value") }

        val problem =
            ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for ${errors.size} field(s)",
            )
        problem.title = "Validation Error"
        problem.type = URI.create("/problems/validation-error")
        problem.setProperty("timestamp", Instant.now())
        problem.setProperty("path", extractPath(request))
        problem.setProperty("errors", errors)
        return problem
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest,
    ): ProblemDetail {
        val problem =
            ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.message ?: "An unexpected error occurred",
            )
        problem.title = "Internal Server Error"
        problem.type = URI.create("/problems/internal-error")
        problem.setProperty("timestamp", Instant.now())
        problem.setProperty("path", extractPath(request))
        return problem
    }

    /**
     * Extract request URI from WebRequest description.
     * Format: "uri=/api/v1/games/123" → "/api/v1/games/123"
     */
    private fun extractPath(request: WebRequest): String = request.getDescription(false).removePrefix("uri=")
}

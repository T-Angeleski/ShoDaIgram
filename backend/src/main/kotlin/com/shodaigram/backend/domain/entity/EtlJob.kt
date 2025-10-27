package com.shodaigram.backend.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
@Table(name = "etl_jobs")
data class EtlJob(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 10)
    val source: DataSource,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: JobStatus = JobStatus.RUNNING,

    @Column(name = "total_records", nullable = false)
    val totalRecords: Int = 0,

    @Column(name = "processed_records", nullable = false)
    var processedRecords: Int = 0,

    @Column(name = "failed_records", nullable = false)
    var failedRecords: Int = 0,

    @Column(name = "error_message")
    var errorMessage: String? = null,

    @Column(name = "error_details")
    val errorDetails: String? = null,

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "started_at", nullable = false)
    val startedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,
) {
    fun markCompleted() {
        status = JobStatus.COMPLETED
        completedAt = LocalDateTime.now()
    }

    fun markFailed(error: String) {
        status = JobStatus.FAILED
        errorMessage = error
        completedAt = LocalDateTime.now()
    }
}

enum class DataSource {
    IGDB,
    RAWG
}

enum class JobStatus {
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

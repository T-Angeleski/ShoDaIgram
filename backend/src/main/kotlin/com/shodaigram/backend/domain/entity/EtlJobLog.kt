package com.shodaigram.backend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "etl_job_logs")
data class EtlJobLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    val job: EtlJob,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val logLevel: LogLevel,
    @Column(nullable = false, length = 2000)
    val message: String,
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    enum class LogLevel {
        INFO,
        WARN,
        ERROR,
    }
}

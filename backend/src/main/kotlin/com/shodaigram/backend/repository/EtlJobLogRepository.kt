package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.EtlJobLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EtlJobLogRepository : JpaRepository<EtlJobLog, Long> {
    fun findByJobIdOrderByCreatedAtDesc(jobId: Long): List<EtlJobLog>

    fun findByJobIdAndLogLevelOrderByCreatedAtDesc(
        jobId: Long,
        logLevel: EtlJobLog.LogLevel,
    ): List<EtlJobLog>

    fun findByJobIdOrderByCreatedAtDesc(
        jobId: Long,
        pageable: Pageable,
    ): Page<EtlJobLog>

    /**
     * Count errors/warnings for a specific job.
     */
    @Query("SELECT COUNT(e) FROM EtlJobLog e WHERE e.job.id = :jobId AND e.logLevel IN ('WARN', 'ERROR')")
    fun countIssuesByJobId(jobId: Long): Long

    /**
     * Get latest N logs for monitoring during ETL job execution.
     * Uses Pageable to limit results (JPQL does not support LIMIT).
     */
    @Query(
        """
    SELECT e FROM EtlJobLog e
    WHERE e.job.id = :jobId
    ORDER BY e.createdAt DESC
    """,
    )
    fun findLatestLogsByJobId(
        jobId: Long,
        pageable: Pageable,
    ): List<EtlJobLog>

    /**
     * Find all WARN/ERROR logs across all jobs
     */
    @Query(
        """
        SELECT e FROM EtlJobLog e
        WHERE e.logLevel IN ('WARN', 'ERROR')
        ORDER BY e.createdAt DESC
        """,
    )
    fun findAllIssues(pageable: Pageable): Page<EtlJobLog>
}

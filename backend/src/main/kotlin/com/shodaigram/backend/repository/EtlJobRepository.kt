package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.EtlJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EtlJobRepository : JpaRepository<EtlJob, Long> {
    /**
     * Find latest job by source (RAWG, IGDB, or merged).
     */
    fun findFirstBySourceOrderByStartedAtDesc(source: String): EtlJob?

    /**
     * Find all jobs with status IN_PROGRESS or PENDING (for monitoring).
     */
    @Query("SELECT e FROM EtlJob e WHERE e.status IN ('in_progress', 'pending') ORDER BY e.startedAt DESC")
    fun findActiveJobs(): List<EtlJob>

    /**
     * Find jobs that failed within last N days (for alerting).
     */
    @Query(
        """
        SELECT e FROM EtlJob e
        WHERE e.status = 'failed'
        AND e.startedAt > :since
        ORDER BY e.startedAt DESC
        """,
    )
    fun findRecentFailures(since: LocalDateTime): List<EtlJob>

    /**
     * Get job completion statistics (for dashboard).
     */
    @Query(
        """
        SELECT e.status as status, COUNT(e) as count
        FROM EtlJob e
        GROUP BY e.status
        """,
    )
    fun getJobStatistics(): List<Map<String, Any>>
}

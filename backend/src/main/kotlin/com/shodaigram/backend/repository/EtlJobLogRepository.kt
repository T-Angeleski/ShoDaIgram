package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.EtlJobLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for ETL job logs (INFO/WARN/ERROR events).
 * Used for debugging ETL pipeline issues.
 */
@Repository
interface EtlJobLogRepository : JpaRepository<EtlJobLog, Long>

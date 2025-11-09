package com.shodaigram.backend.repository

import com.shodaigram.backend.domain.entity.EtlJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for ETL job tracking.
 * Stores job status, record counts, and error messages.
 */
@Repository
interface EtlJobRepository : JpaRepository<EtlJob, Long>

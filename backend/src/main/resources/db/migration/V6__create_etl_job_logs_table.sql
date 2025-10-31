CREATE TABLE etl_job_logs
(
    id         BIGSERIAL PRIMARY KEY,

    job_id     BIGINT        NOT NULL,
    CONSTRAINT fk_etl_job_logs_job FOREIGN KEY (job_id) REFERENCES etl_jobs (id) ON DELETE CASCADE,

    log_level  VARCHAR(10)   NOT NULL CHECK (log_level IN ('INFO', 'WARN', 'ERROR')),

    message    VARCHAR(2000) NOT NULL,

    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_etl_job_logs_job_id ON etl_job_logs (job_id, created_at DESC);
CREATE INDEX idx_etl_job_logs_level ON etl_job_logs (log_level, created_at DESC);
CREATE INDEX idx_etl_job_logs_issues ON etl_job_logs (job_id, created_at DESC)
    WHERE log_level IN ('WARN', 'ERROR');

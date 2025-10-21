CREATE TABLE etl_jobs
(
    id                BIGSERIAL PRIMARY KEY,

    source            VARCHAR(10) NOT NULL CHECK (source IN ('igdb', 'rawg', 'merged')),
    status            VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'completed', 'failed', 'partial')),

    total_records     INTEGER     NOT NULL DEFAULT 0,
    processed_records INTEGER     NOT NULL DEFAULT 0,
    failed_records    INTEGER     NOT NULL DEFAULT 0,

    error_message     TEXT,
    error_details     TEXT,

    started_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at      TIMESTAMP,
    duration_seconds  INTEGER GENERATED ALWAYS AS (
        CASE
            WHEN completed_at IS NOT NULL
                THEN EXTRACT(EPOCH FROM (completed_at - started_at))::INTEGER
            END
        ) STORED,

    CONSTRAINT check_record_counts CHECK (processed_records + failed_records <= total_records)
);

CREATE INDEX idx_etl_jobs_status ON etl_jobs (status);
CREATE INDEX idx_etl_jobs_started_at_desc ON etl_jobs (started_at DESC);

CREATE TABLE game_similarities
(
    id               BIGSERIAL PRIMARY KEY,

    game_id          BIGINT        NOT NULL REFERENCES games (id) ON DELETE CASCADE,
    similar_game_id  BIGINT        NOT NULL REFERENCES games (id) ON DELETE CASCADE,

    similarity_score NUMERIC(5, 4) NOT NULL CHECK (similarity_score >= 0 AND similarity_score <= 1),
    similarity_type  VARCHAR(20)   NOT NULL DEFAULT 'API_PROVIDED'
        CHECK (similarity_type IN ('PRECOMPUTED_TF_IDF', 'API_PROVIDED', 'TAG_BASED')),

    computed_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_game_pair UNIQUE (game_id, similar_game_id),
    CONSTRAINT no_self_similarity CHECK (game_id <> similar_game_id)
);

CREATE INDEX idx_similarities_game_score ON game_similarities (game_id, similarity_score DESC);
CREATE INDEX idx_similarities_type ON game_similarities (similarity_type);

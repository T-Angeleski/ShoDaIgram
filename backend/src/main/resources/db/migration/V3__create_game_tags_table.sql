CREATE TABLE game_tags
(
    id         BIGSERIAL PRIMARY KEY,

    game_id    BIGINT    NOT NULL REFERENCES games (id) ON DELETE CASCADE,
    tag_id     BIGINT    NOT NULL REFERENCES tags (id) ON DELETE CASCADE,

    weight     NUMERIC(3, 2)      DEFAULT 1.0 CHECK (weight > 0 AND weight <= 1.0),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_game_tag UNIQUE (game_id, tag_id)
);

CREATE INDEX idx_game_tags_game_id ON game_tags (game_id);
CREATE INDEX idx_game_tags_tag_id ON game_tags (tag_id);

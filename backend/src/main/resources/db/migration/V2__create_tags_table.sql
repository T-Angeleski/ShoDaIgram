CREATE TABLE tags
(
    id              BIGSERIAL PRIMARY KEY,

    name            VARCHAR(100) NOT NULL,
    normalized_name VARCHAR(100) NOT NULL,
    category        VARCHAR(50)  NOT NULL CHECK ( category IN ('genre', 'theme', 'platform', 'game_mode', 'keyword',
                                                               'player_perspective', 'franchise', 'developer',
                                                               'publisher'
        ) ),

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_tag_per_category UNIQUE (normalized_name, category)
);

CREATE INDEX idx_tags_category ON tags (category);
CREATE INDEX idx_tags_normalized_name ON tags (normalized_name);

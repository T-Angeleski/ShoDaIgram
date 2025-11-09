CREATE TABLE tags
(
    id              BIGSERIAL PRIMARY KEY,

    name            VARCHAR(100) NOT NULL,
    normalized_name VARCHAR(100) NOT NULL,
    category        VARCHAR(50)  NOT NULL CHECK ( category IN ('GENRE', 'THEME', 'PLATFORM', 'GAME_MODE', 'KEYWORD',
                                                               'PLAYER_PERSPECTIVE', 'FRANCHISE', 'DEVELOPER',
                                                               'PUBLISHER'
        ) ),

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_tag_per_category UNIQUE (normalized_name, category)
);

CREATE INDEX idx_tags_category ON tags (category);
CREATE INDEX idx_tags_normalized_name ON tags (normalized_name);

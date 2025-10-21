CREATE TABLE games
(
    id                   BIGSERIAL PRIMARY KEY,

    igdb_id              BIGINT UNIQUE,
    rawg_id              BIGINT UNIQUE,

    name                 VARCHAR(500) NOT NULL,
    slug                 VARCHAR(500) NOT NULL UNIQUE,
    description          TEXT,

    release_date         DATE,
    rating               DECIMAL(5, 2) CHECK ( rating >= 0 AND rating <= 10 ),
    rating_count         INTEGER,

    background_image_url TEXT,
    website_url          TEXT,

    search_vector        TSVECTOR,

    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_external_id CHECK (igdb_id IS NOT NULL OR rawg_id IS NOT NULL)
);

CREATE INDEX idx_games_name ON games (name);
CREATE INDEX idx_games_rating ON games (rating DESC NULLS LAST);
CREATE INDEX idx_games_release_date ON games (release_date DESC NULLS LAST);
CREATE INDEX idx_games_search_gin ON games USING GIN (search_vector);

-- Trigger for automatic tsvector update
CREATE OR REPLACE FUNCTION update_game_search_vector() RETURNS TRIGGER AS
$$
BEGIN
    NEW.search_vector :=
            SETWEIGHT(TO_TSVECTOR('english', COALESCE(NEW.name, '')), 'A') ||
            SETWEIGHT(TO_TSVECTOR('english', COALESCE(NEW.description, '')), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER games_search_vector_update
    BEFORE INSERT OR UPDATE OF name, description
    ON games
    FOR EACH ROW
EXECUTE FUNCTION update_game_search_vector();

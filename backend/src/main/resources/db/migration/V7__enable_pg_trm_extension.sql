-- Enable trigram extension for fuzzy string matching
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_games_name_trgm ON games USING gin (LOWER(name) gin_trgm_ops);

CREATE OR REPLACE FUNCTION game_name_similarity(name1 TEXT, name2 TEXT)
    RETURNS REAL AS
$$
BEGIN
    RETURN SIMILARITY(LOWER(name1), LOWER(name2));
END;
$$ LANGUAGE plpgsql IMMUTABLE;

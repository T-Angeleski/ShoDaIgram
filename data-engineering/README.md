# 🎮 Sho Da Igram - Data Pipeline

Game data collection pipeline that fetches comprehensive game information from RAWG and IGDB APIs and saves it as structured JSON for further processing.

## Quick Start

1. **Setup project:**

   ```bash
   make setup
   ```

2. **Configure API keys:**

   ```bash
   # Edit .env with your API credentials
   # RAWG: Get free key from https://rawg.io/apidocs
   # IGDB: Create Twitch app at https://dev.twitch.tv/console
   ```

3. **Generate IGDB token:**

   ```bash
   make get-token
   ```

4. **Validate setup:**

   ```bash
   make validate-env
   ```

5. **Test data fetching:**
   ```bash
   make test-rawg
   make test-igdb
   ```

## Data Collection

```bash
# Fetch from specific API
make fetch-rawg    # RAWG data only → rawg_games_*.json
make fetch-igdb    # IGDB data only → igdb_games_*.json

# Fetch from both APIs
make fetch-all     # Both APIs → creates both files

# Inspect collected data
make show-data
```

## Development Workflow

```bash
# Format and lint code
make dev

# Clean cache files
make clean
```

## Environment Variables

| Variable             | Description                              | Required | Default |
| -------------------- | ---------------------------------------- | -------- | ------- |
| `RAWG_API_KEY`       | RAWG API key for higher rate limits      | Yes      | None    |
| `RAWG_RATE_LIMIT`    | Seconds between RAWG requests            | No       | 1.0     |
| `IGDB_CLIENT_ID`     | Twitch Client ID for IGDB                | Yes      | None    |
| `IGDB_CLIENT_SECRET` | Twitch Client Secret for IGDB            | Yes      | None    |
| `IGDB_ACCESS_TOKEN`  | Generated access token                   | Auto     | None    |
| `IGDB_RATE_LIMIT`    | Seconds between IGDB requests            | No       | 0.25    |
| `DATA_DIR`           | Directory for output JSON files          | No       | data    |
| `FETCH_LIMIT`        | Max games to fetch per run               | No       | 100     |
| `LOG_LEVEL`          | Logging level (DEBUG/INFO/WARNING/ERROR) | No       | INFO    |

## Getting API Keys

### RAWG API

1. Visit [https://rawg.io/apidocs](https://rawg.io/apidocs)
2. Create account and get free API key
3. Add to `.env` as `RAWG_API_KEY`

### IGDB API (via Twitch)

1. Go to [Twitch Developer Console](https://dev.twitch.tv/console)
2. Create new application
3. Copy Client ID and Client Secret to `.env`
4. Run `make get-token` to generate access token

## Commands Reference

```bash
make help          # Show all available commands
make setup         # Complete project setup
make get-token     # Generate IGDB access token
make validate-env  # Check environment configuration
make dev           # Format and lint code
make clean         # Remove cache files
make clean-data    # Delete all data files (destructive!)
```

## Output Data Structure

The pipeline generates timestamped JSON files:

- `rawg_games_YYYYMMDD_HHMMSS.json` - RAWG game data
- `igdb_games_YYYYMMDD_HHMMSS.json` - IGDB game data

Each file contains an array of game objects with metadata including genres, platforms, ratings, developers, and more.

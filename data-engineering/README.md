## Setup

1. **Install dependencies:**
   ```bash
   make dev-install
   ```

2. **Configure environment:**
   ```bash
   # Copy the sample environment file
   cp .env.sample .env

   # Edit .env and add your RAWG API key (optional but recommended)
   # Get a free key from: https://rawg.io/apidocs
   ```

3. **Setup pre-commit hooks:**
   ```bash
   make pre-commit-install
   ```

4. **Test the pipeline:**
   ```bash
   make fetch-data
   ```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `RAWG_API_KEY` | RAWG API key for higher rate limits | None |
| `API_RATE_LIMIT` | Seconds between API requests | 1.0 |
| `DATA_DIR` | Directory for output data | data |
| `FETCH_LIMIT` | Max games to fetch | 100 |
| `LOG_LEVEL` | Logging level (DEBUG/INFO/WARNING/ERROR) | INFO |

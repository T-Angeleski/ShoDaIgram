# Sho Da Igram 🎮

> "What game should I play?" - A work-in-progress game recommendation system

Ever spend more time browsing game libraries than actually playing? This project aims to fix that by building a smart recommendation engine that actually understands what you like.


## What's Working Right Now

### ✅ Data Collection Pipeline (Python)
Scraping comprehensive game information from RAWG and IGDB APIs:
- Game metadata (titles, descriptions, release dates)
- Genre and platform information
- User ratings and review counts
- Developer/publisher details
- Similar game relationships

Already collected: **~10,000+ game records** as structured JSON files.

### 🔨 Backend API (Kotlin + Spring Boot)
Setting up the foundation:
- PostgreSQL database schema design
- REST API endpoints (in progress)
- Data models and repository layer

## Tech Stack

**Data Pipeline:**
- Python 3.12 with httpx for API calls
- JSON for structured data storage
- Automated rate limiting and error handling

**Backend:**
- Kotlin 2.0.10
- Spring Boot 3.5.6
- PostgreSQL 16
- Apache Lucene 9.10.0 (for search)

**Code Quality:**
- Ktlint & Detekt for Kotlin
- Black & Flake8 for Python
- Pre-commit hooks

## Project Structure

```
ShoDaIgram/
├── data-engineering/     # 🐍 Python - Data collection scripts
│   ├── src/             # Fetchers for RAWG/IGDB APIs
│   ├── data/            # ~10k games in JSON format
│   └── Makefile         # make fetch-all, make dev, etc.
│
└── backend/             # 🔧 Kotlin - API server (in development)
    ├── src/main/kotlin  # Spring Boot application
    └── build.gradle.kts # Gradle build config
```

## Getting Started

### 1. Collect Game Data

```bash
cd data-engineering
make setup              # Install dependencies
make get-token          # Generate IGDB API token
make fetch-all          # Start collecting games (takes ~5-10 min)
make show-data          # See what you got
```

### 2. Run the Backend (WIP)

```bash
cd backend
./gradlew bootRun
```

## What's Next?

- [ ] **ETL Pipeline** - Transform raw JSON into normalized PostgreSQL tables
- [ ] **Search API** - Lucene-powered game search endpoint
- [ ] **Recommendation Engine** - Collaborative filtering based on genres/tags
- [ ] **Frontend** - Simple web UI to test recommendations

## Why These APIs?

**RAWG.io** - Great for popular games, user ratings, and screenshots. Free tier is generous.

**IGDB** - More detailed metadata like game modes, franchises, and age ratings. Requires Twitch developer account (also free).


## Sample Data

Here's what the pipeline collects for each game:

```json
{
  "name": "The Witcher 3: Wild Hunt",
  "slug": "the-witcher-3-wild-hunt",
  "rating": 92.5,
  "genres": ["Action", "RPG"],
  "platforms": ["PC", "PlayStation 4", "Xbox One", "Nintendo Switch"],
  "developers": ["CD PROJEKT RED"],
  "publishers": ["CD PROJEKT RED"],
  "themes": ["Fantasy", "Open World"],
  "game_modes": ["Single player"],
  "player_perspectives": ["Third person"],
  "similar_games": ["The Elder Scrolls V: Skyrim", "Dragon Age: Inquisition"],
  "first_release_date": "2015-05-19",
  "data_source": "igdb"
}
```

# Sho Da Igram 🎮

A video game recommendation system that collects game data from multiple APIs and provides intelligent recommendations.

<!-- Build & Quality -->

[![Detekt](https://img.shields.io/badge/code%20style-detekt-blue)](https://detekt.dev)
[![Ktlint](https://img.shields.io/badge/code%20style-ktlint-blue)](https://pinterest.github.io/ktlint)

<!-- Tech Stack -->

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?logo=springboot&logoColor=white)](https://spring.io)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Lucene](https://img.shields.io/badge/Apache%20Lucene-9.10.0-D22128?logo=apache&logoColor=white)](https://lucene.apache.org)

<!-- Project Info -->

[![License](https://img.shields.io/github/license/T-Angeleski/ShoDaIgram)](LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/T-Angeleski/ShoDaIgram)](https://github.com/T-Angeleski/ShoDaIgram/commits/main)

## Project Structure

```
ShoDaIgram/
├── data-engineering/     # Data collection pipeline (✅ Active)
│   ├── src/             # Python source code
│   ├── data/            # JSON output files
│   └── Makefile         # Development commands
├── backend/             # API
└── frontend/            # Web interface (📋 Planned)
```

## Current Features

### Data Engineering Pipeline

- **Multi-API Integration**: Fetches from RAWG and IGDB APIs
- **Rich Game Data**: Genres, platforms, ratings, developers, publishers
- **JSON Output**: Structured data ready for processing
- **Rate Limiting**: Respects API limits automatically
- **Comprehensive Logging**: Full pipeline visibility

## Quick Start

```bash
# Setup data pipeline
cd data-engineering
make setup

# Configure API keys in .env
make get-token

# Collect game data
make fetch-all

# View collected data
make show-data
```

## Development Status

| Component               | Status         | Description                    |
| ----------------------- | -------------- | ------------------------------ |
| 📥 **Data Engineering** | ✅ **Active**  | Multi-API game data collection |
| 🔄 **ETL Pipeline**     | 🚧 **Next**    | Data transformation & loading  |
| 🌐 **Backend API**      | 📋 **Planned** | FastAPI recommendation service |
| 🎨 **Frontend**         | 📋 **Planned** | React web interface            |

## Data Sources

- **[RAWG](https://rawg.io/)** - Game database with ratings, screenshots, reviews
- **[IGDB](https://www.igdb.com/)** - Comprehensive game metadata and relationships

## Example Output

```json
{
  "name": "The Witcher 3: Wild Hunt",
  "rating": 96,
  "genres": ["Action", "RPG"],
  "platforms": ["PC", "PlayStation", "Xbox"],
  "developers": ["CD PROJEKT RED"],
  "data_source": "igdb"
}
```

---

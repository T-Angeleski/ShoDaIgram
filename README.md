# Sho Da Igram 🎮

> "What game should I play?" - A work-in-progress game recommendation system

Ever spend more time browsing game libraries than actually playing? This project aims to fix that by building a smart recommendation engine that actually understands what you like.

## 🎯 Features

- **Similar Games Discovery**: Find games similar to your favorites using TF-IDF content-based recommendations
- **Natural Language Search**: Search for games by describing what you want (e.g., "space shooter with crafting")
- **Tag-Based Filtering**: Filter games by genres, themes, platforms, and more
- **Explainable Recommendations**: See why games were recommended with match reason badges

## 🏗️ Architecture

### Backend
- **Framework**: Spring Boot 3.5.6 (Kotlin)
- **Database**: PostgreSQL with full-text search
- **Algorithms**: TF-IDF (Apache Lucene), BM25 (PostgreSQL)
- **Data**: ~7,500 games from IGDB and RAWG APIs

### Frontend
- **Framework**: React 18 + TypeScript + Vite
- **UI Library**: Material-UI (MUI) v5
- **State Management**: TanStack Query (React Query)
- **Styling**: Styled Components + MUI theming

### Data Engineering
- **Language**: Python 3.12
- **APIs**: IGDB, RAWG
- **Processing**: ETL pipeline for game data ingestion

## 🚀 Getting Started

### Prerequisites
- Java 21
- Node.js 20+
- PostgreSQL 15+
- Yarn
- Python 3.12 (for data engineering)

### Backend Setup
```bash
cd backend
./gradlew bootRun
```

API runs on http://localhost:8080

Frontend Setup
```bash
cd frontend
yarn install
yarn dev
```
App runs on http://localhost:5173

Data Engineering
```bash
cd data-engineering
uv sync
make fetch  # Fetch game data from APIs
```

📁 Project Structure
```
ShoDaIgram/
├── backend/           # Spring Boot API
├── frontend/          # React TypeScript app
└── data-engineering/  # Python ETL scripts
```

"""Main entry point for the data engineering pipeline."""

import sys
from pathlib import Path

from loguru import logger

from src.sho_da_igram.data.fetcher import GameDataFetcher
from src.sho_da_igram.utils.config import Config


def main():
    """Run the data pipeline."""
    print("🎮 Sho Da Igram - Data Pipeline")
    print("Fetching game data from RAWG API...")

    config = Config.from_env()
    config.setup_logging()

    Path(config.data_dir).mkdir(parents=True, exist_ok=True)
    fetcher = GameDataFetcher(config)

    try:
        logger.info("Starting data fetch process")
        output_file = fetcher.fetch_and_save_popular_games(limit=50)

        print(f"✅ Pipeline completed! Data saved to: {output_file}")

    except Exception as e:
        logger.error(f"Pipeline failed: {e}")
        print(f"❌ Pipeline failed: {e}")
        sys.exit(1)

    finally:
        fetcher.close()


if __name__ == "__main__":
    main()

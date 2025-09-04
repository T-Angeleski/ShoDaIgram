"""Main entry point"""

import sys
from pathlib import Path

from loguru import logger

from src.sho_da_igram.data.fetcher import GameDataFetcher
from src.sho_da_igram.utils.config import Config


def setup_environment(config: Config) -> None:
    """Setup the environment for the pipeline."""
    Path(config.data_dir).mkdir(parents=True, exist_ok=True)

    config.setup_logging()

    logger.info(f"Data directory: {config.data_dir}")
    logger.info(f"Fetch limit: {config.fetch_limit}")


def run_pipeline(config: Config) -> Path:
    """Run the main data pipeline."""
    fetcher = GameDataFetcher(config)

    try:
        logger.info("Starting data fetch process")
        output_file = fetcher.fetch_games_to_csv(limit=config.fetch_limit)
        logger.info("Pipeline completed successfully")
        return output_file

    finally:
        fetcher.close()


def main():
    """Run the data pipeline."""
    print("🎮 Sho Da Igram - Data Pipeline")
    print("Fetching game data from RAWG API...")

    try:
        config = Config.from_env()
        setup_environment(config)

        output_file = run_pipeline(config)

        print(f"✅ Pipeline completed! Data saved to: {output_file}")

    except ValueError as e:
        logger.error(f"Configuration error: {e}")
        print(f"❌ Configuration error: {e}")
        sys.exit(1)

    except Exception as e:
        logger.error(f"Pipeline failed: {e}")
        print(f"❌ Pipeline failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()

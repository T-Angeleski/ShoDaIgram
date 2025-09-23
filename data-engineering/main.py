"""Main entry point"""

import sys
from pathlib import Path

from loguru import logger

from src.sho_da_igram.data.fetcher import IGDBDataFetcher, RAWGDataFetcher
from src.sho_da_igram.utils.config import Config


def setup_environment(config: Config) -> None:
    """Setup the environment for the pipeline."""
    Path(config.data_dir).mkdir(parents=True, exist_ok=True)

    config.setup_logging()

    logger.info(f"Data directory: {config.data_dir}")
    logger.info(f"Fetch limit: {config.fetch_limit}")


def run_rawg_pipeline(config: Config) -> Path:
    """Run the RAWG data pipeline."""
    fetcher = RAWGDataFetcher(config)

    try:
        logger.info("Starting RAWG data fetch process")
        output_file = fetcher.fetch_games_to_json(limit=config.fetch_limit)
        logger.info("RAWG pipeline completed successfully")
        return output_file

    finally:
        fetcher.close()


def run_igdb_pipeline(config: Config) -> Path:
    """Run the IGDB data pipeline."""
    fetcher = IGDBDataFetcher(config)

    try:
        logger.info("Starting IGDB data fetch process")
        output_file = fetcher.fetch_games_to_json(limit=config.fetch_limit)
        logger.info("IGDB pipeline completed successfully")
        return output_file

    finally:
        fetcher.close()


def main():
    """Run the data pipeline."""
    print("🎮 Sho Da Igram - Data Pipeline")

    # Get pipeline choice from command line or default to both
    pipeline_type = sys.argv[1] if len(sys.argv) > 1 else "both"

    if pipeline_type not in ["rawg", "igdb", "both"]:
        print("❌ Invalid pipeline type. Use: rawg, igdb, or both")
        sys.exit(1)

    print(f"Running {pipeline_type.upper()} pipeline...")

    try:
        config = Config.from_env()
        setup_environment(config)

        if pipeline_type in ["rawg", "both"]:
            print("\n📥 Fetching game data from RAWG API...")
            rawg_output = run_rawg_pipeline(config)
            print(f"✅ RAWG data saved to: {rawg_output}")

        if pipeline_type in ["igdb", "both"]:
            print("\n📥 Fetching game data from IGDB API...")
            igdb_output = run_igdb_pipeline(config)
            print(f"✅ IGDB data saved to: {igdb_output}")

        print("\n🎉 Pipeline(s) completed successfully!")

    except ValueError as e:
        logger.error(f"Configuration error: {e}")
        print(f"❌ Configuration error: {e}")
        print("💡 Make sure to set required API keys in your .env file")
        sys.exit(1)

    except Exception as e:
        logger.error(f"Pipeline failed: {e}")
        print(f"❌ Pipeline failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()

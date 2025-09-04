"""Data fetcher that saves raw data to CSV."""

from pathlib import Path
from typing import Any, Dict, List

from loguru import logger

from ..api.api_client import RAWGClient
from ..utils.config import Config
from ..utils.utils import CSVHandler, GameDataHandler


class GameDataFetcher:
    """Fetches game data and saves to CSV"""

    DEFAULT_PAGE_SIZE = 40
    DEFAULT_ORDERING = "-rating"

    def __init__(self, config: Config):
        self.config = config
        self.client = RAWGClient(
            api_key=config.rawg_api_key, rate_limit=config.api_rate_limit
        )
        self.output_dir = Path(config.data_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

    def _fetch_games_batch(self, limit: int) -> List[Dict[str, Any]]:
        """
        Fetch games in batches from the API.

        Args:
            limit: Maximum number of games to fetch

        Returns:
            List of flattened game data

        Raises:
            ValueError: If limit is not positive
        """
        if limit <= 0:
            raise ValueError("Limit must be positive")

        all_games: List[Dict[str, Any]] = []
        page = 1
        fetched = 0

        while fetched < limit:
            logger.info(f"Fetching page {page}...")

            page_size = min(self.DEFAULT_PAGE_SIZE, limit - fetched)

            try:
                response = self.client.get_games_page(
                    page=page, page_size=page_size, ordering=self.DEFAULT_ORDERING
                )
            except Exception as e:
                logger.error(f"Failed to fetch page {page}: {e}")
                break

            games = response.get("results", [])
            if not games:
                logger.info("No more games available")
                break

            for game in games:
                try:
                    flattened = GameDataHandler.flatten_game_data(game)
                    all_games.append(flattened)
                    fetched += 1
                    if fetched >= limit:
                        break
                except Exception as e:
                    logger.warning(
                        f"Failed to process game {game.get('id', 'unknown')}: {e}"
                    )
                    continue

            page += 1

        return all_games

    def fetch_games_to_csv(self, limit: int = 100, output_filename: str = "") -> Path:
        """
        Fetch games and save directly to CSV.

        Args:
            limit: Max number of games to fetch (must be positive)
            output_filename: Custom filename (optional)

        Returns:
            Path to saved CSV file

        Raises:
            ValueError: If limit is not positive
            IOError: If file cannot be written
        """
        filename = CSVHandler.generate_timestamped_filename("games", output_filename)
        output_path = self.output_dir / filename

        logger.info(f"Fetching {limit} games to {output_path}")

        games = self._fetch_games_batch(limit)
        CSVHandler.save_to_csv(games, output_path)

        return output_path

    def fetch_detailed_games_to_csv(
        self, game_ids: List[int], output_filename: str = ""
    ) -> Path:
        """
        Fetch detailed game info for specific IDs.

        Args:
            game_ids: List of RAWG game IDs (must not be empty)
            output_filename: Custom filename (optional)

        Returns:
            Path to saved CSV file

        Raises:
            ValueError: If game_ids is empty
            IOError: If file cannot be written
        """
        if not game_ids:
            raise ValueError("game_ids list cannot be empty")

        filename = CSVHandler.generate_timestamped_filename(
            "games_detailed", output_filename
        )
        output_path = self.output_dir / filename

        logger.info(f"Fetching detailed data for {len(game_ids)} games")

        detailed_games: List[Dict[str, Any]] = []

        for game_id in game_ids:
            try:
                logger.debug(f"Fetching details for game {game_id}")
                game_data = self.client.get_game_details(game_id)
                flattened = GameDataHandler.flatten_game_data(game_data)
                detailed_games.append(flattened)
            except Exception as e:
                logger.error(f"Failed to fetch game {game_id}: {e}")
                continue

        CSVHandler.save_to_csv(detailed_games, output_path)
        return output_path

    def close(self) -> None:
        """Close the client."""
        self.client.close()

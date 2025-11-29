"""Data fetcher that saves raw data to CSV."""

from pathlib import Path
from typing import Any, Dict, List

from loguru import logger

from ..api.igdb_client import IGDBClient
from ..api.rawg_client import RAWGClient
from ..utils.config import Config
from ..utils.utils import IGDBDataHandler, JsonUtils, RAWGDataHandler


class RAWGDataFetcher:
    """Fetches game data from RAWG and parses to JSON"""

    DEFAULT_PAGE_SIZE = 40
    DEFAULT_ORDERING = "-added"

    def __init__(self, config: Config):
        if not config.rawg_api_key:
            raise ValueError("RAWG API key is required")

        self.config = config
        self.client = RAWGClient(
            api_key=config.rawg_api_key, rate_limit=config.rawg_rate_limit
        )
        self.output_dir = Path(config.data_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

    def _fetch_games_batch(self, limit: int) -> List[Dict[str, Any]]:
        """
        Fetch games in batches from the API.

        Args:
            limit: Maximum number of games to fetch

        Returns:
            List of processed game data

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
                    # Fetch detailed game info to get description_raw
                    game_id = game.get("id")
                    if game_id:
                        logger.debug(f"Fetching detailed info for game {game_id}")
                        detailed_game = self.client.get_game_details(game_id)
                        processed = RAWGDataHandler.process_game_data(detailed_game)
                    else:
                        processed = RAWGDataHandler.process_game_data(game)

                    all_games.append(processed)
                    fetched += 1
                    if fetched >= limit:
                        break
                except Exception as e:
                    logger.warning(
                        f"Failed to process game {game.get('id', 'unknown')}: {e}"
                    )
                    continue

            page += 1

        logger.info(f"Successfully fetched {len(all_games)} games from RAWG")
        return all_games

    def fetch_games_to_json(self, limit: int = 100, output_filename: str = "") -> Path:
        """
        Fetch games and save directly to JSON.

        Args:
            limit: Max number of games to fetch (must be positive)
            output_filename: Custom filename (optional)

        Returns:
            Path to saved JSON file

        Raises:
            ValueError: If limit is not positive
            IOError: If file cannot be written
        """
        filename = JsonUtils.generate_timestamped_filename(
            "rawg_games", output_filename
        )
        output_path = self.output_dir / filename

        logger.info(f"Fetching {limit} games to {output_path}")

        games = self._fetch_games_batch(limit)

        if not games:
            logger.warning("No games fetched from RAWG")
            JsonUtils.save_to_json([], output_path)
            return output_path

        JsonUtils.save_to_json(games, output_path)
        return output_path

    def fetch_detailed_games_to_json(
        self, game_ids: List[int], output_filename: str = ""
    ) -> Path:
        """
        Fetch detailed game info for specific IDs.

        Args:
            game_ids: List of RAWG game IDs (must not be empty)
            output_filename: Custom filename (optional)

        Returns:
            Path to saved JSON file

        Raises:
            ValueError: If game_ids is empty
            IOError: If file cannot be written
        """
        if not game_ids:
            raise ValueError("game_ids list cannot be empty")

        filename = JsonUtils.generate_timestamped_filename(
            "rawg_games_detailed", output_filename
        )
        output_path = self.output_dir / filename

        logger.info(f"Fetching detailed data for {len(game_ids)} games")

        detailed_games: List[Dict[str, Any]] = []

        for game_id in game_ids:
            try:
                logger.debug(f"Fetching details for game {game_id}")
                game_data = self.client.get_game_details(game_id)
                processed = RAWGDataHandler.process_game_data(game_data)
                detailed_games.append(processed)
            except Exception as e:
                logger.error(f"Failed to fetch game {game_id}: {e}")
                continue

        JsonUtils.save_to_json(detailed_games, output_path)
        return output_path

    def close(self) -> None:
        """Close the client."""
        self.client.close()


class IGDBDataFetcher:
    """Fetches game data from IGDB and parses it to JSON"""

    def __init__(self, config: Config) -> None:
        """
        Initialize IGDB fetcher

        Args:
            config: Application configuration

        Raises:
            ValueError: If credentials are missing
        """
        if not config.igdb_client_id or not config.igdb_access_token:
            raise ValueError("IGDB client ID and access token are required")

        self.config = config
        self.client = IGDBClient(
            client_id=config.igdb_client_id,
            access_token=config.igdb_access_token,
            rate_limit=config.igdb_rate_limit,
        )
        self.output_dir = Path(config.data_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

    def _fetch_games_batch(
        self, limit: int, min_rating: int = 70
    ) -> List[Dict[str, Any]]:
        """
        Fetch games in batches

        Args:
            limit: Maximum number of games to fetch
            min_rating: Minimum rating threshold

        Returns:
            List of processed game data

        Raises:
            ValueError: If limit is not positive
        """
        if limit <= 0:
            raise ValueError("Limit must be positive")

        all_games: List[Dict[str, Any]] = []
        offset = 0
        fetched = 0
        batch_size = min(500, limit)
        while fetched < limit:
            remaining = limit - fetched
            current_batch_size = min(batch_size, remaining)

            logger.info(
                f"Fetching IGDB batch: offset={offset}, limit={current_batch_size}"
            )

            try:
                response = self.client.get_top_games(
                    limit=current_batch_size, offset=offset, min_rating=min_rating
                )
            except Exception as e:
                logger.error(f"Failed to fetch IGDB batch at offset {offset}: {e}")
                break

            if not response:
                logger.info("No more games available from IGDB")
                break

            for game in response:
                try:
                    processed = IGDBDataHandler.process_game_data(game)
                    all_games.append(processed)
                    fetched += 1
                    if fetched >= limit:
                        break
                except Exception as e:
                    logger.warning(
                        f"Failed to process IGDB game {game.get('id', 'unknown')}: {e}"
                    )
                    continue

            offset += current_batch_size

            # If we got fewer results than requested, we've reached the end
            if len(response) < current_batch_size:
                logger.info("Reached end of IGDB results")
                break

        logger.info(f"Successfully fetched {len(all_games)} games from IGDB")
        return all_games

    def fetch_games_to_json(
        self, limit: int = 100, output_filename: str = "", min_rating: int = 70
    ) -> Path:
        """
        Fetch games from IGDB and save to JSON.

        Args:
            limit: Max number of games to fetch (must be positive)
            output_filename: Custom filename (optional)
            min_rating: Minimum game rating threshold

        Returns:
            Path to saved JSON file

        Raises:
            ValueError: If limit is not positive
            IOError: If file cannot be written
        """
        filename = JsonUtils.generate_timestamped_filename(
            "igdb_games", output_filename
        )
        output_path = self.output_dir / filename

        logger.info(f"Fetching {limit} games from IGDB to {output_path}")

        games = self._fetch_games_batch(limit, min_rating)

        if not games:
            logger.warning("No games fetched from IGDB")
            JsonUtils.save_to_json([], output_path)
            return output_path

        JsonUtils.save_to_json(games, output_path)
        return output_path

    def close(self) -> None:
        """Close the IGDB client."""
        self.client.close()

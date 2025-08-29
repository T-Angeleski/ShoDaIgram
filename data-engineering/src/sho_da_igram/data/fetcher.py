"""Data fetcher that saves raw data to CSV."""

import csv
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List

from loguru import logger

from ..api.api_client import RAWGClient
from ..utils.config import Config


class GameDataFetcher:
    """Fetches game data and saves to CSV"""

    def __init__(self, config: Config):
        self.config = config
        self.client = RAWGClient(
            api_key=config.rawg_api_key, rate_limit=config.api_rate_limit
        )
        self.output_dir = Path(config.data_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

    def _flatten_game_data(self, game: Dict[str, Any]) -> Dict[str, Any]:
        """Flatten nested game data for CSV"""
        flattened = {
            "id": game.get("id"),
            "name": game.get("name"),
            "slug": game.get("slug"),
            "released": game.get("released"),
            "rating": game.get("rating"),
            "rating_top": game.get("rating_top"),
            "ratings_count": game.get("ratings_count"),
            "metacritic": game.get("metacritic"),
            "background_image": game.get("background_image"),
            "website": game.get("website"),
            "description_raw": game.get("description_raw"),
            "updated": game.get("updated"),
            "playtime": game.get("playtime"),
            "achievements_count": game.get("achievements_count"),
            "creators_count": game.get("creators_count"),
            "additions_count": game.get("additions_count"),
            "game_series_count": game.get("game_series_count"),
            "user_game": game.get("user_game"),
        }

        if "genres" in game and game["genres"]:
            genres_list = game["genres"]
            flattened["genres"] = ",".join([g.get("name", "") for g in genres_list])
            flattened["genre_ids"] = ",".join(
                [str(g.get("id", "")) for g in genres_list]
            )

        if "platforms" in game and game["platforms"]:
            platforms = [
                p.get("platform", {}).get("name", "") for p in game["platforms"]
            ]
            flattened["platforms"] = ",".join(platforms)

        if "developers" in game and game["developers"]:
            flattened["developers"] = ",".join(
                [d.get("name", "") for d in game["developers"]]
            )

        if "publishers" in game and game["publishers"]:
            flattened["publishers"] = ",".join(
                [p.get("name", "") for p in game["publishers"]]
            )

        if "tags" in game and game["tags"]:
            flattened["tags"] = ",".join([t.get("name", "") for t in game["tags"]])

        # Add fetch metadata
        flattened["fetched_at"] = datetime.now(timezone.utc).isoformat()

        return flattened

    def fetch_games_to_csv(self, limit: int = 100, output_filename: str = "") -> Path:
        """
        Fetch games and save directly to CSV.

        Args:
            limit: Max number of games to fetch
            output_filename: Custom filename (optional)

        Returns:
            Path to saved CSV file
        """
        if not output_filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            output_filename = f"games_{timestamp}.csv"

        output_path = self.output_dir / output_filename

        logger.info(f"Fetching {limit} games to {output_path}")

        all_games: List[Dict[str, Any]] = []
        page = 1
        fetched = 0

        while fetched < limit:
            logger.info(f"Fetching page {page}...")

            response = self.client.get_games_page(
                page=page, page_size=min(40, limit - fetched), ordering="-rating"
            )

            games = response.get("results", [])
            if not games:
                logger.info("No more games available")
                break

            for game in games:
                try:
                    flattened = self._flatten_game_data(game)
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

        if all_games:
            fieldnames = all_games[0].keys()

            with open(output_path, "w", newline="", encoding="utf-8") as csvfile:
                writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
                writer.writeheader()
                writer.writerows(all_games)

            logger.info(f"Saved {len(all_games)} games to {output_path}")
        else:
            logger.warning("No games to save")

        return output_path

    def fetch_detailed_games_to_csv(
        self, game_ids: List[int], output_filename: str = ""
    ) -> Path:
        """
        Fetch detailed game info for specific IDs.

        Args:
            game_ids: List of RAWG game IDs
            output_filename: Custom filename

        Returns:
            Path to saved CSV file
        """
        if not output_filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            output_filename = f"games_detailed_{timestamp}.csv"

        output_path = self.output_dir / output_filename

        logger.info(f"Fetching detailed data for {len(game_ids)} games")

        detailed_games: List[Dict[str, Any]] = []

        for game_id in game_ids:
            try:
                logger.debug(f"Fetching details for game {game_id}")
                game_data = self.client.get_game_details(game_id)
                flattened = self._flatten_game_data(game_data)
                detailed_games.append(flattened)
            except Exception as e:
                logger.error(f"Failed to fetch game {game_id}: {e}")
                continue

        if detailed_games:
            fieldnames = detailed_games[0].keys()

            with open(output_path, "w", newline="", encoding="utf-8") as csvfile:
                writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
                writer.writeheader()
                writer.writerows(detailed_games)

            logger.info(f"Saved {len(detailed_games)} detailed games to {output_path}")
        else:
            logger.warning("No detailed games to save")

        return output_path

    def close(self):
        """Close the client."""
        self.client.close()

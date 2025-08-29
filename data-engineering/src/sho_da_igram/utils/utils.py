"""CSV  and game data utilities"""

import csv
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List, Optional

from loguru import logger


class CSVHandler:
    """Handles CSV file operations"""

    DEFAULT_ENCODING = "utf-8"

    @staticmethod
    def generate_timestamped_filename(
        prefix: str, custom_name: Optional[str] = None
    ) -> str:
        """Generate a timestamped filename"""
        if custom_name:
            return custom_name
        timestamp = datetime.now(timezone.utc).strftime("%Y%m%d_%H%M%S")
        return f"{prefix}_{timestamp}.csv"

    @staticmethod
    def save_to_csv(
        data: List[Dict[str, Any]], output_path: Path, encoding: str = DEFAULT_ENCODING
    ) -> None:
        """
        Save list of dictionaries to a CSV file.

        Args:
            data: List of dictionaries to save
            output_path: Path to the output CSV file
            encoding: File encoding

        Raises:
            ValueError: If data is empty
            IOError: If file cannot be written
        """
        if not data:
            logger.warning("No data to save")
            raise ValueError("No data to save")

        try:
            fieldnames = data[0].keys()

            with open(output_path, "w", newline="", encoding=encoding) as csvfile:
                writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
                writer.writeheader()
                writer.writerows(data)

            logger.info(f"Saved {len(data)} records to {output_path}")
        except IOError as e:
            logger.error(f"Failed to write to {output_path}: {e}")
            raise
        except Exception as e:
            logger.error(f"Unexpected error while saving to CSV: {e}")
            raise


class GameDataHandler:
    """Processes game data"""

    # Always present
    CORE_FIELDS = [
        "id",
        "name",
        "slug",
        "released",
        "rating",
        "rating_top",
        "ratings_count",
        "metacritic",
        "background_image",
        "website",
        "description_raw",
        "updated",
        "playtime",
        "achievements_count",
        "creators_count",
        "additions_count",
        "game_series_count",
        "user_game",
    ]

    @staticmethod
    def extract_core_fields(game: Dict[str, Any]) -> Dict[str, Any]:
        """Extract core game fields"""
        return {field: game.get(field) for field in GameDataHandler.CORE_FIELDS}

    @staticmethod
    def extract_list_fields(
        game: Dict[str, Any], field_name: str, name_key: str = "name"
    ) -> str:
        """Extract and join list-based fields"""
        field_data = game.get(field_name)
        if not field_data:
            return ""
        return ",".join(item.get(name_key, "") for item in field_data)

    @staticmethod
    def extract_platform_names(game: Dict[str, Any]) -> str:
        """Extract platform names with special handling for nested structure"""
        platforms = game.get("platforms")
        if not platforms:
            return ""

        platform_names = [p.get("platform", {}).get("name", "") for p in platforms]

        return ",".join(platform_names)

    @staticmethod
    def extract_genre_ids(game: Dict[str, Any]) -> str:
        """Extract genre IDs as comma-separated string."""
        genres = game.get("genres")
        if not genres:
            return ""
        return ",".join(str(g.get("id", "")) for g in genres)

    @classmethod
    def flatten_game_data(cls, game: Dict[str, Any]) -> Dict[str, Any]:
        """
        Flatten nested game data for CSV

        Args:
            game: Raw game data from API

        Returns:
            Flattened dictionary

        Raises:
            ValueError: If game data is invalid
        """

        if not game or not game.get("id"):
            raise ValueError("Invalid game data: missing required fields")

        flattened = cls.extract_core_fields(game)

        flattened.update(
            {
                "genres": cls.extract_list_fields(game, "genres"),
                "genre_ids": cls.extract_genre_ids(game),
                "platforms": cls.extract_platform_names(game),
                "developers": cls.extract_list_fields(game, "developers"),
                "publishers": cls.extract_list_fields(game, "publishers"),
                "tags": cls.extract_list_fields(game, "tags"),
            }
        )

        flattened["fetched_at"] = datetime.now(timezone.utc).isoformat()

        return flattened

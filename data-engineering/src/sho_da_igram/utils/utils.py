"""CSV  and game data utilities"""

import json
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List, Optional

from loguru import logger


class JsonUtils:
    """Handles JSON file operations"""

    DEFAULT_ENCODING = "utf-8"

    @staticmethod
    def generate_timestamped_filename(
        prefix: str, custom_name: Optional[str] = None
    ) -> str:
        """Generate a timestamped filename"""
        if custom_name:
            return custom_name
        timestamp = datetime.now(timezone.utc).strftime("%Y%m%d_%H%M%S")
        return f"{prefix}_{timestamp}.json"

    @staticmethod
    def save_to_json(
        data: List[Dict[str, Any]],
        output_path: Path,
        encoding: str = DEFAULT_ENCODING,
        indent: int = 2,
    ) -> None:
        """
        Save list of dictionaries to a JSON file.

        Args:
            data: List of dictionaries to save
            output_path: Path to the output JSON file
            encoding: File encoding
            indent: JSON indentation level

        Raises:
            ValueError: If data is empty
            IOError: If file cannot be written
        """
        if not data:
            logger.warning("No data to save")
            raise ValueError("No data to save")

        try:
            with open(output_path, "w", encoding=encoding) as jsonfile:
                json.dump(
                    data, jsonfile, indent=indent, ensure_ascii=False, default=str
                )

            logger.info(f"Saved {len(data)} records to {output_path}")
        except IOError as e:
            logger.error(f"Failed to write to {output_path}: {e}")
            raise
        except Exception as e:
            logger.error(f"Unexpected error while saving to JSON: {e}")
            raise


class RAWGDataHandler:
    """Processes RAWG game data"""

    @staticmethod
    def extract_list_field_names(items: Optional[List[Dict[str, Any]]]) -> List[str]:
        """Extract names from a list of objects"""
        if not items:
            return []
        return [item.get("name", "") for item in items if item.get("name")]

    @staticmethod
    def extract_platform_names(game: Dict[str, Any]) -> List[str]:
        """Extract platform names from game data"""
        platforms = game.get("platforms", [])
        if not platforms:
            return []

        platform_names = [
            platform_info.get("platform", {}).get("name", "")
            for platform_info in platforms
            if platform_info.get("platform", {}).get("name")
        ]

        return platform_names

    @staticmethod
    def extract_genre_info(game: Dict[str, Any]) -> Dict[str, List[Any]]:
        """Extract genre names and IDs from game data"""
        genres = game.get("genres", [])
        if not genres:
            return {"names": [], "ids": []}

        return {
            "names": [genre.get("name", "") for genre in genres if genre.get("name")],
            "ids": [genre.get("id") for genre in genres if genre.get("id")],
        }

    @classmethod
    def process_game_data(cls, game: Dict[str, Any]) -> Dict[str, Any]:
        """
        Process and structure game data

        Args:
            game: Game data from API

        Returns:
            Processed game data dictionary

        Raises:
            ValueError: If game data is invalid
        """
        if not game or not game.get("id"):
            raise ValueError("Invalid game data: missing required fields")

        genre_info = cls.extract_genre_info(game)

        processed: Dict[str, Any] = {
            "rawg_id": game.get("id"),
            "name": game.get("name"),
            "slug": game.get("slug"),
            "released": game.get("released"),
            "rating": game.get("rating"),
            "rating_top": game.get("rating_top"),
            "ratings_count": game.get("ratings_count"),
            "metacritic": game.get("metacritic"),
            "description_raw": game.get("description_raw"),
            "background_image": game.get("background_image"),
            "website": game.get("website"),
            "playtime": game.get("playtime"),
            "achievements_count": game.get("achievements_count"),
        }

        # Extract list-based fields
        processed.update(
            {
                "genres": genre_info["names"],
                "genre_ids": genre_info["ids"],
                "platforms": cls.extract_platform_names(game),
                "developers": cls.extract_list_field_names(game.get("developers")),
                "publishers": cls.extract_list_field_names(game.get("publishers")),
                "tags": cls.extract_list_field_names(game.get("tags")),
            }
        )

        # Add additional counts
        processed.update(
            {
                "creators_count": game.get("creators_count"),
                "additions_count": game.get("additions_count"),
                "game_series_count": game.get("game_series_count"),
                "user_game": game.get("user_game"),
                "updated": game.get("updated"),
            }
        )

        # Add metadata
        processed["fetched_at"] = datetime.now(timezone.utc).isoformat()
        processed["data_source"] = "rawg"

        return processed


class IGDBDataHandler:
    """Processes IGDB game data"""

    @staticmethod
    def extract_names_from_list(items: Optional[List[Dict[str, Any]]]) -> List[str]:
        """Extract names from a list of objects"""
        if not items:
            return []
        return [item.get("name", "") for item in items if item.get("name")]

    @staticmethod
    def extract_companies_by_role(
        companies: Optional[List[Dict[str, Any]]], role: str
    ) -> List[str]:
        """Extract company names filtered by role (developer/publisher)"""
        if not companies:
            return []

        role_key = "developer" if role.lower() == "developer" else "publisher"

        return [
            company.get("company", {}).get("name", "")
            for company in companies
            if company.get(role_key) and company.get("company", {}).get("name")
        ]

    @staticmethod
    def format_release_date(timestamp: Optional[int]) -> Optional[str]:
        """Convert timestamp to ISO date string"""
        if not timestamp:
            return None
        try:
            return datetime.fromtimestamp(timestamp, tz=timezone.utc).date().isoformat()
        except (ValueError, OSError):
            return None

    @staticmethod
    def extract_age_ratings(ratings: Optional[List[Dict[str, Any]]]) -> Dict[str, int]:
        """Extract and organize age ratings"""
        if not ratings:
            return {}

        organized_ratings: Dict[str, int] = {}
        for rating in ratings:
            category = rating.get("category")
            rating_value = rating.get("rating")

            if isinstance(category, int):
                category_name = {
                    1: "ESRB",
                    2: "PEGI",
                    3: "CERO",
                    4: "USK",
                    5: "OFLC",
                }.get(category, f"category_{category}")

                if category_name and rating_value is not None:
                    organized_ratings[category_name] = rating_value

        return organized_ratings

    @classmethod
    def process_game_data(cls, game: Dict[str, Any]) -> Dict[str, Any]:
        """
        Process and clean IGDB game data

        Args:
            game: Raw game data from IGDB API

        Returns:
            Processed game data dictionary

        Raises:
            ValueError: If game data is invalid
        """
        if not game or not game.get("id"):
            raise ValueError("Invalid game data: missing required fields")

        # Extract basic fields
        processed: Dict[str, Any] = {
            "igdb_id": game.get("id"),
            "name": game.get("name"),
            "slug": game.get("slug"),
            "summary": game.get("summary"),
            "storyline": game.get("storyline"),
            "url": game.get("url"),
            "first_release_date": cls.format_release_date(
                game.get("first_release_date")
            ),
            "rating": game.get("rating"),
            "rating_count": game.get("rating_count"),
            "total_rating": game.get("total_rating"),
            "total_rating_count": game.get("total_rating_count"),
        }

        # Extract list-based fields
        processed.update(
            {
                "genres": cls.extract_names_from_list(game.get("genres")),
                "platforms": cls.extract_names_from_list(game.get("platforms")),
                "themes": cls.extract_names_from_list(game.get("themes")),
                "game_modes": cls.extract_names_from_list(game.get("game_modes")),
                "franchises": cls.extract_names_from_list(game.get("franchises")),
                "keywords": cls.extract_names_from_list(game.get("keywords")),
                "player_perspectives": cls.extract_names_from_list(
                    game.get("player_perspectives")
                ),
                "game_engines": cls.extract_names_from_list(game.get("game_engines")),
                "similar_games": cls.extract_names_from_list(game.get("similar_games")),
            }
        )

        # Extract companies
        companies = game.get("involved_companies", [])
        processed.update(
            {
                "developers": cls.extract_companies_by_role(companies, "developer"),
                "publishers": cls.extract_companies_by_role(companies, "publisher"),
            }
        )

        processed["age_ratings"] = cls.extract_age_ratings(game.get("age_ratings"))

        # Add collection info
        collection = game.get("collection")
        processed["collection"] = (
            [collection.get("name")] if collection and collection.get("name") else []
        )
        # Add metadata
        processed["fetched_at"] = datetime.now(timezone.utc).isoformat()
        processed["data_source"] = "igdb"

        return processed

"""IGDB API client"""

import time
from typing import Any, Dict, List, Optional

import httpx
from loguru import logger

BASE_API_URL = "https://api.igdb.com/v4"


class IGDBClient:
    """Client for interacting with IGDB API"""

    def __init__(
        self, client_id: str, access_token: str, rate_limit: float = 0.25
    ) -> None:
        """
        Initialize the client

        Args:
          client_id: Twitch Client ID
          access_token: Twitch Access Token
          rate_limit: Seconds to wait between requests
        """
        if not client_id or not access_token:
            raise ValueError("Client ID and Access Token must be provided")

        self.base_url = BASE_API_URL
        self.client_id = client_id
        self.access_token = access_token
        self.rate_limit = rate_limit
        self._last_request_time = 0.0

        self.client = httpx.Client(
            timeout=30.0,
            headers={
                "Client-ID": client_id,
                "Authorization": f"Bearer {access_token}",
                "Accept": "application/json",
            },
        )

        logger.info(f"Initialized IGDB client with rate limit: {rate_limit}s")

    def _wait_for_rate_limit(self) -> None:
        """Ensure we respect the rate limit"""
        time_since_last = time.time() - self._last_request_time
        if time_since_last < self.rate_limit:
            wait_time = self.rate_limit - time_since_last
            logger.debug(f"Rate limiting: waiting {wait_time:.2f}s")
            time.sleep(wait_time)
        self._last_request_time = time.time()

    def _make_request(self, endpoint: str, query: str) -> List[Dict[str, Any]]:
        """
        Make a request to the API

        Args:
          endpoint: API endpoint to call
          query: IGDB query string

        Returns:
          List of JSON response data

        Raises:
          httpx.HTTPError: If request fails
          Exception: For any other errors
        """
        self._wait_for_rate_limit()

        url = f"{self.base_url}/{endpoint}"

        try:
            response = self.client.post(url, content=query)
            response.raise_for_status()
            return response.json()
        except httpx.HTTPError as e:
            logger.error(f"IGDB request failed for {endpoint}: {e}")
            logger.error(f"Query was: {query}")
            raise
        except Exception as e:
            logger.error(f"Unexpected error for {endpoint}: {e}")
            raise

    def get_top_games(
        self, limit: int = 100, offset: int = 0, min_rating: int = 70
    ) -> List[Dict[str, Any]]:
        """
        Get top-rated games with comprehensive data

        Args:
          limit: Number of games to retrieve (max 500 per request)
          offset: Offset for pagination
          min_rating: Minimum rating threshold

        Returns:
          List of game data dictionaries
        """
        request_limit = min(limit, 500)

        query = f"""
        fields
            name,
            slug,
            summary,
            storyline,
            first_release_date,
            rating,
            rating_count,
            total_rating,
            total_rating_count,
            url,
            genres.name,
            genres.slug,
            platforms.name,
            platforms.slug,
            platforms.platform_family,
            themes.name,
            themes.slug,
            game_modes.name,
            game_modes.slug,
            age_ratings.rating,
            age_ratings.category,
            age_ratings.content_descriptions.description,
            franchises.name,
            franchises.slug,
            collection.name,
            collection.slug,
            similar_games.name,
            similar_games.slug,
            keywords.name,
            keywords.slug,
            player_perspectives.name,
            player_perspectives.slug,
            game_engines.name,
            game_engines.slug,
            involved_companies.company.name,
            involved_companies.developer,
            involved_companies.publisher,
            release_dates.date,
            release_dates.region,
            release_dates.platform.name;
        where rating >= {min_rating} & rating_count >= 10;
        sort rating desc;
        limit {request_limit};
        offset {offset};
        """

        return self._make_request("games", query)

    def get_game_by_id(self, game_id: int) -> Optional[Dict[str, Any]]:
        """
        Get detailed game information by IGDB ID

        Args:
            game_id: IGDB game ID

        Returns:
            Game data dictionary or None if not found
        """
        query = f"""
        fields
            name,
            slug,
            summary,
            storyline,
            first_release_date,
            rating,
            rating_count,
            total_rating,
            total_rating_count,
            url,
            genres.name,
            genres.slug,
            platforms.name,
            platforms.slug,
            themes.name,
            themes.slug,
            game_modes.name,
            game_modes.slug,
            age_ratings.rating,
            age_ratings.category,
            franchises.name,
            franchises.slug,
            similar_games.name,
            similar_games.slug,
            keywords.name,
            keywords.slug,
            player_perspectives.name,
            player_perspectives.slug,
            game_engines.name,
            game_engines.slug,
            involved_companies.company.name,
            involved_companies.developer,
            involved_companies.publisher;
        where id = {game_id};
        """

        results = self._make_request("games", query)
        return results[0] if results else None

    def search_games(self, search_term: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Search for games by name

        Args:
            search_term: Search query
            limit: Maximum results to return

        Returns:
            List of matching games
        """
        escaped_term = search_term.replace('"', '\\"')

        query = f"""
        search "{escaped_term}";
        fields
            name,
            slug,
            summary,
            first_release_date,
            rating,
            rating_count,
            genres.name,
            platforms.name;
        limit {limit};
        """

        return self._make_request("games", query)

    def close(self) -> None:
        """Close the HTTP client"""
        self.client.close()
        logger.debug("IGDB client closed")

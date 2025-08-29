"""RAWG API Client for fetching video game data"""

import time
from typing import Any, Dict, List, Optional, Union
from urllib.parse import urlencode

import httpx
from loguru import logger
from pydantic import BaseModel, Field

BASE_API_URL = "https://api.rawg.io/api"


class GameResponse(BaseModel):
    """Response model for game data from API."""

    id: int
    name: str
    slug: str
    releaseDate: Optional[str] = None
    rating: float = 0.0
    rating_top: int = 0
    ratings_count: int = 0
    metacritic: Optional[int] = None
    genres: List[Dict[str, Any]] = Field(default_factory=lambda: [])
    platforms: List[Dict[str, Any]] = Field(default_factory=lambda: [])
    developers: List[Dict[str, Any]] = Field(default_factory=lambda: [])
    publishers: List[Dict[str, Any]] = Field(default_factory=lambda: [])
    tags: List[Dict[str, Any]] = Field(default_factory=lambda: [])
    background_image: Optional[str] = None
    description: Optional[str] = None


class RAWGClient:
    """Client for interacting with API"""

    def __init__(self, api_key: Optional[str] = None, rate_limit: float = 1.0):
        """
        Initialize the RAWG API client.

        Args:
            api_key (Optional[str]): API key for authentication.
            rate_limit (float): Seconds to wait between requests.
        """

        self.base_url = BASE_API_URL
        self.api_key = api_key
        self.rate_limit = rate_limit
        self.client = httpx.Client(timeout=30.0)
        self._last_request_time = 0.0

        logger.info(f"Initialized RAWG client with rate limit: {rate_limit}s")

    def _wait_for_rate_limit(self) -> None:
        """Ensure we respect the rate limit"""
        time_since_last = time.time() - self._last_request_time
        if time_since_last < self.rate_limit:
            wait_time = self.rate_limit - time_since_last
            logger.debug(f"Rate limiting: waiting {wait_time:.2f}s")
            time.sleep(wait_time)
        self._last_request_time = time.time()

    def _make_request(
        self, endpoint: str, params: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Make a request to the API

        Args:
            endpoint (str): API endpoint to call.
            params (Dict[str, Any]): Query parameters for the request.

        Returns:
          JSON response data
        """
        self._wait_for_rate_limit()

        params = params or {}
        if self.api_key:
            params["key"] = self.api_key

        url = f"{self.base_url}/{endpoint}"
        if params:
            url += f"?{urlencode(params)}"

        logger.debug(f"Making request to: {url}")

        try:
            response = self.client.get(url)
            response.raise_for_status()
            return response.json()
        except httpx.HTTPStatusError as e:
            logger.error(f"HTTP error: {e.response.status_code} - {e.response.text}")
            raise
        except Exception as e:
            logger.error(f"Request failed: {str(e)}")
            raise

    def get_games(
        self,
        page: int = 1,
        page_size: int = 20,
        search: Optional[str] = None,
        genres: Optional[str] = None,
        platforms: Optional[str] = None,
        ordering: str = "-rating",
    ) -> Dict[str, Any]:
        """
        Get games from RAWG API.

        Args:
            page: Page number (1-based)
            page_size: Number of games per page (max 40)
            search: Search query
            genres: Comma-separated genre IDs
            platforms: Comma-separated platform IDs
            ordering: Sort order (e.g., '-rating', 'name', '-released')

        Returns:
            API response with games data
        """
        params: Dict[str, Union[str, int]] = {
            "page": page,
            "page_size": min(page_size, 40),  # API limit
            "ordering": ordering,
        }

        if search:
            params["search"] = search
        if genres:
            params["genres"] = genres
        if platforms:
            params["platforms"] = platforms

        logger.info(f"Fetching games: page={page}, size={page_size}")
        return self._make_request("games", params)

    def get_game_details(self, game_id: int) -> Dict[str, Any]:
        """
        Get detailed information for a specific game.

        Args:
            game_id: RAWG game ID

        Returns:
            Detailed game data
        """
        logger.info(f"Fetching details for game ID: {game_id}")
        return self._make_request(f"games/{game_id}")

    def get_genres(self) -> Dict[str, Any]:
        """Get all available genres."""
        logger.info("Fetching genres")
        return self._make_request("genres")

    def get_platforms(self) -> Dict[str, Any]:
        """Get all available platforms."""
        logger.info("Fetching platforms")
        return self._make_request("platforms")

    def search_games(self, query: str, limit: int = 10) -> List[GameResponse]:
        """
        Search for games and return structured results.

        Args:
            query (str): Search term.
            limit (int): Maximum number of results to return.

        Returns:
            List of game responses
        """
        response = self.get_games(search=query, page_size=limit)
        games: List[GameResponse] = []

        for game_data in response.get("results", []):
            try:
                game = GameResponse(**game_data)
                games.append(game)
            except Exception as e:
                logger.error(f"Error parsing game data: {e} - Data: {game_data}")
                continue

        logger.info(f"Found {len(games)} games for query '{query}'")
        return games

    def close(self) -> None:
        """Close the HTTP client."""
        self.client.close()
        logger.info("Closed RAWG client")

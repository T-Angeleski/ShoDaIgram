"""RAWG API Client for fetching video game data"""

import time
from typing import Any, Dict, Optional

import httpx
from loguru import logger

BASE_API_URL = "https://api.rawg.io/api"


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

        try:
            response = self.client.get(url, params=params)
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"Request failed for {endpoint}: {e}")
            raise

    def get_games_page(
        self, page: int = 1, page_size: int = 40, **filters: Any
    ) -> Dict[str, Any]:
        """Get one page of games with optional filters."""
        params: Dict[str, Any] = {
            "page": page,
            "page_size": min(page_size, 40),
            **filters,
        }
        return self._make_request("games", params)

    def get_game_details(self, game_id: int) -> Dict[str, Any]:
        """Get detailed game info."""
        return self._make_request(f"games/{game_id}")

    def close(self):
        """Close the client."""
        self.client.close()

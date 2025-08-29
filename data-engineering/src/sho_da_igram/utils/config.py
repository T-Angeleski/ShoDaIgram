"""Config settings"""

import os
from pathlib import Path
from typing import Optional

from dotenv import load_dotenv
from pydantic import BaseModel

load_dotenv()


class Config(BaseModel):
    """Simple config for data fetching."""

    rawg_api_key: Optional[str] = None
    api_rate_limit: float = 1.0

    data_dir: str = "data"
    fetch_limit: int = 100

    log_level: str = "INFO"
    log_to_file: bool = True
    log_file: str = "logs/pipeline.log"

    @classmethod
    def from_env(cls) -> "Config":
        """Load from environment."""
        return cls(
            rawg_api_key=os.getenv("RAWG_API_KEY"),
            api_rate_limit=float(os.getenv("API_RATE_LIMIT", "1.0")),
            data_dir=os.getenv("DATA_DIR", "data"),
            fetch_limit=int(os.getenv("FETCH_LIMIT", "100")),
            log_level=os.getenv("LOG_LEVEL", "INFO"),
            log_to_file=os.getenv("LOG_TO_FILE", "true").lower() == "true",
            log_file=os.getenv("LOG_FILE", "logs/pipeline.log"),
        )

    def setup_logging(self) -> None:
        """Setup basic logging."""
        from loguru import logger

        logger.remove()
        logger.add(
            sink=lambda msg: print(msg, end=""),
            level=self.log_level,
            format="<green>{time:HH:mm:ss}</green> | <level>{level}</level> | "
            "{message}",
        )

        if self.log_to_file:
            Path(self.log_file).parent.mkdir(parents=True, exist_ok=True)
            logger.add(self.log_file, level=self.log_level, rotation="1 day")

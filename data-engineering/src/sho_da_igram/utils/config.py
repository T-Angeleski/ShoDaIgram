"""Config settings"""

import os
from pathlib import Path
from typing import Optional

from pydantic import BaseModel


class Config(BaseModel):
    """Configuration"""

    rawg_api_key: Optional[str] = None
    api_rate_limit: float = 1.0

    data_dir: str = "data"

    log_level: str = "INFO"
    log_to_file: bool = True
    log_file: str = "logs/pipeline.log"

    @classmethod
    def from_env(cls) -> "Config":
        """Create config from environment variables"""
        return cls(
            rawg_api_key=os.getenv("RAWG_API_KEY"),
            api_rate_limit=float(os.getenv("API_RATE_LIMIT", 1.0)),
            data_dir=os.getenv("DATA_DIR", "data"),
            log_level=os.getenv("LOG_LEVEL", "INFO"),
            log_to_file=os.getenv("LOG_TO_FILE", "true").lower() == "true",
            log_file=os.getenv("LOG_FILE", "logs/pipeline.log"),
        )

    def setup_logging(self) -> None:
        """Setup logging configuration."""
        from loguru import logger

        logger.remove()

        logger.add(
            sink=lambda msg: print(msg, end=""),
            level=self.log_level,
            format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> "
            "| <level>{level: <8}</level> "
            "| <cyan>{name}</cyan>:<cyan>{function}</cyan>"
            ":<cyan>{line}</cyan> - <level>{message}</level>",
        )

        if self.log_to_file:
            log_path = Path(self.log_file)
            log_path.parent.mkdir(parents=True, exist_ok=True)

            logger.add(
                sink=self.log_file,
                level=self.log_level,
                rotation="1 day",
                retention="7 days",
                format="{time:YYYY-MM-DD HH:mm:ss} | {level: <8} "
                "| {name}:{function}:{line} - {message}",
            )

        logger.info(
            f"Logging configured: level={self.log_level}, file={self.log_to_file}"
        )

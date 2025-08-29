"""Config settings"""

import os
from pathlib import Path
from typing import Optional

from dotenv import load_dotenv
from pydantic import BaseModel

load_dotenv()


class Config(BaseModel):
    """Configuration"""

    rawg_api_key: Optional[str] = None
    api_rate_limit: float = 1.0
    api_timeout: float = 30.0

    data_dir: str = "data"
    fetch_limit: int = 100
    batch_size: int = 20
    output_format: str = "json"

    log_level: str = "INFO"
    log_to_file: bool = True
    log_file: str = "logs/pipeline.log"

    debug: bool = False
    verbose: bool = False
    environment: str = "development"

    @classmethod
    def from_env(cls) -> "Config":
        """Create config from environment variables"""
        return cls(
            rawg_api_key=os.getenv("RAWG_API_KEY"),
            api_rate_limit=float(os.getenv("API_RATE_LIMIT", "1.0")),
            api_timeout=float(os.getenv("API_TIMEOUT", "30.0")),
            data_dir=os.getenv("DATA_DIR", "data"),
            fetch_limit=int(os.getenv("FETCH_LIMIT", "100")),
            batch_size=int(os.getenv("BATCH_SIZE", "20")),
            output_format=os.getenv("OUTPUT_FORMAT", "json"),
            log_level=os.getenv("LOG_LEVEL", "INFO"),
            log_to_file=os.getenv("LOG_TO_FILE", "true").lower() == "true",
            log_file=os.getenv("LOG_FILE", "logs/pipeline.log"),
            debug=os.getenv("DEBUG", "false").lower() == "true",
            verbose=os.getenv("VERBOSE", "false").lower() == "true",
            environment=os.getenv("ENVIRONMENT", "development"),
        )

    def setup_logging(self) -> None:
        """Setup logging configuration."""
        from loguru import logger

        logger.remove()

        if self.environment == "production":
            format_str = (
                "{time:YYYY-MM-DD HH:mm:ss} | {level: <8} | "
                "{name}:{function}:{line} - {message}"
            )
        else:
            format_str = (
                "<green>{time:YYYY-MM-DD HH:mm:ss}</green> | "
                "<level>{level: <8}</level> | "
                "<cyan>{name}</cyan>:<cyan>{function}</cyan>:"
                "<cyan>{line}</cyan> - <level>{message}</level>"
            )

        logger.add(
            sink=lambda msg: print(msg, end=""), level=self.log_level, format=format_str
        )

        if self.log_to_file:
            log_path = Path(self.log_file)
            log_path.parent.mkdir(parents=True, exist_ok=True)

            logger.add(
                sink=self.log_file,
                level=self.log_level,
                rotation="1 day",
                retention="7 days",
                format=(
                    "{time:YYYY-MM-DD HH:mm:ss} | {level: <8} | "
                    "{name}:{function}:{line} - {message}"
                ),
            )

        if self.debug:
            logger.info(f"Configuration loaded: {self.model_dump_json(indent=2)}")
        else:
            logger.info(
                f"Logging configured: level={self.log_level}, file={self.log_to_file}"
            )

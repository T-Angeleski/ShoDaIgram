"""Game scraper for collecting video game data."""

from loguru import logger


class Scraper:
    """Simple scraper class for collecting video game data."""

    def __init__(self):
        """Initialize the scraper."""
        pass

    def scrape(self) -> None:
        """
        Scrape video game data.

        Currently just logs the process.
        """
        logger.info("Starting the scraping process...")

        # (https://quotes.toscrape.com/)

        logger.info("Completed")


if __name__ == "__main__":
    scraper = Scraper()
    scraper.scrape()

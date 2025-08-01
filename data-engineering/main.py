"""Main entry point for the data engineering pipeline."""

from src.sho_da_igram.scraping.game_scraper import Scraper


def main():
    """Run the data pipeline."""
    print("🎮 Sho Da Igram - Data Pipeline")
    print("Starting scraping...")

    scraper = Scraper()
    scraper.scrape()

    print("✅ Pipeline completed!")


if __name__ == "__main__":
    main()

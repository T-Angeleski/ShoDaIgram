.PHONY: help install dev-install lint format clean scrape pre-commit-install pre-commit-run

GREEN := \033[0;32m
YELLOW := \033[0;33m
NC := \033[0m

help:  ## Show this help message
	@echo "$(GREEN)Sho Da Igram - Data Engineering$(NC)"
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'

install:  ## Install production dependencies
	uv sync --frozen

dev-install:  ## Install development dependencies
	uv sync --frozen --all-extras

lint:  ## Run linting
	uv run flake8 src/ main.py

format:  ## Format code
	uv run black src/ main.py
	uv run isort src/ main.py

format-check:  ## Check formatting
	uv run black --check src/ main.py
	uv run isort --check-only src/ main.py

pre-commit-install:  ## Install pre-commit hooks
	uv run pre-commit install

pre-commit-run:  ## Run pre-commit on all files
	uv run pre-commit run --all-files

scrape:  ## Run scraping pipeline
	@echo "$(GREEN)Running scraping pipeline...$(NC)"
	uv run python main.py

clean:  ## Clean cache and temporary files
	find . -type d -name "__pycache__" -delete
	find . -type f -name "*.pyc" -delete
	find . -type d -name "*.egg-info" -exec rm -rf {} +
	rm -rf .pytest_cache/
	rm -rf .coverage
	rm -rf htmlcov/

setup: dev-install pre-commit-install  ## Initial project setup
	@echo "$(GREEN)Setup complete!$(NC)"

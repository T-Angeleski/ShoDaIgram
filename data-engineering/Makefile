.PHONY: help install dev-install lint format format-check clean fetch-data pre-commit-install pre-commit-run check status show-data

# ============================================================================
# COLORS
# ============================================================================
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
BLUE := \033[0;34m
NC := \033[0m

# ============================================================================
# DEFAULT TARGET
# ============================================================================
.DEFAULT_GOAL := help

help:  ## Show this help message
	@echo "$(GREEN)🎮 Sho Da Igram - Data Engineering$(NC)"
	@echo ""
	@echo "$(BLUE)Available commands:$(NC)"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(BLUE)Quick start:$(NC)"
	@echo "  make setup && make fetch-data"

# ============================================================================
# INSTALLATION TARGETS
# ============================================================================
install:  ## Install production dependencies
	@echo "$(GREEN)Installing production dependencies...$(NC)"
	uv sync --frozen

dev-install:  ## Install development dependencies
	@echo "$(GREEN)Installing development dependencies...$(NC)"
	uv sync --frozen --all-extras

# ============================================================================
# CODE QUALITY TARGETS
# ============================================================================
lint:  ## Run linting
	@echo "$(GREEN)Running linting...$(NC)"
	uv run flake8 src/ main.py

format:  ## Format code
	@echo "$(GREEN)Formatting code...$(NC)"
	uv run isort src/ main.py
	uv run black src/ main.py

format-check:  ## Check formatting without making changes
	@echo "$(GREEN)Checking code formatting...$(NC)"
	uv run black --check src/ main.py
	uv run isort --check-only src/ main.py

check: format-check lint  ## Run all code quality checks

# ============================================================================
# PRE-COMMIT TARGETS
# ============================================================================
pre-commit-install:  ## Install pre-commit hooks
	@echo "$(GREEN)Installing pre-commit hooks...$(NC)"
	uv run pre-commit install

pre-commit-run:  ## Run pre-commit on all files
	@echo "$(GREEN)Running pre-commit hooks...$(NC)"
	uv run pre-commit run --all-files

# ============================================================================
# DATA PIPELINE TARGETS
# ============================================================================
fetch-data:  ## Fetch game data from RAWG API to CSV
	@echo "$(GREEN)🎮 Fetching game data from RAWG API...$(NC)"
	@if [ ! -f .env ]; then \
		echo "$(YELLOW)⚠️  Warning: .env file not found. Copy .env.sample to .env and configure.$(NC)"; \
	fi
	uv run python main.py

show-data:  ## Show information about fetched data
	@echo "$(GREEN)📊 Data Summary:$(NC)"
	@if [ -d data ] && [ -n "$$(ls -A data 2>/dev/null)" ]; then \
		echo "$(BLUE)Data files:$(NC)"; \
		ls -lah data/; \
		echo ""; \
		echo "$(BLUE)Latest file preview:$(NC)"; \
		latest_file=$$(ls -t data/*.csv 2>/dev/null | head -1); \
		if [ -n "$$latest_file" ]; then \
			echo "File: $$latest_file"; \
			echo "Rows: $$(wc -l < "$$latest_file")"; \
			echo "First 3 rows:"; \
			head -3 "$$latest_file"; \
		fi; \
	else \
		echo "$(YELLOW)No data files found. Run 'make fetch-data' first.$(NC)"; \
	fi

# ============================================================================
# ENVIRONMENT AND STATUS
# ============================================================================
status:  ## Show project status and configuration
	@echo "$(GREEN)📋 Project Status:$(NC)"
	@echo "$(BLUE)Python version:$(NC) $$(python --version 2>/dev/null || echo 'Not found')"
	@echo "$(BLUE)UV version:$(NC) $$(uv --version 2>/dev/null || echo 'Not found')"
	@echo "$(BLUE)Working directory:$(NC) $$(pwd)"
	@echo "$(BLUE)Environment file:$(NC) $$([ -f .env ] && echo '✅ Found' || echo '❌ Missing')"
	@echo "$(BLUE)Dependencies:$(NC) $$([ -f uv.lock ] && echo '✅ Locked' || echo '❌ Not locked')"
	@echo ""
	@echo "$(BLUE)Data directory:$(NC)"
	@if [ -d data ]; then \
		file_count=$$(ls data/ 2>/dev/null | wc -l); \
		if [ $$file_count -gt 0 ]; then \
			echo "  📁 $$file_count files found"; \
		else \
			echo "  📁 Empty"; \
		fi; \
	else \
		echo "  📁 Not created yet"; \
	fi

validate-env:  ## Validate environment configuration
	@echo "$(GREEN)🔍 Validating environment...$(NC)"
	@if [ ! -f .env ]; then \
		echo "$(RED)❌ .env file not found$(NC)"; \
		echo "$(YELLOW)💡 Copy .env.sample to .env and configure:$(NC)"; \
		echo "   cp .env.sample .env"; \
		exit 1; \
	fi
	@echo "$(GREEN)✅ Environment file found$(NC)"
	@if grep -q "^RAWG_API_KEY=$$" .env; then \
		echo "$(YELLOW)⚠️  RAWG_API_KEY is empty (will use free tier)$(NC)"; \
	else \
		echo "$(GREEN)✅ RAWG_API_KEY configured$(NC)"; \
	fi

# ============================================================================
# CLEANUP TARGETS
# ============================================================================
clean:  ## Clean cache and temporary files
	@echo "$(GREEN)🧹 Cleaning up...$(NC)"
	find . -type d -name "__pycache__" -delete
	find . -type f -name "*.pyc" -delete
	find . -type d -name "*.egg-info" -exec rm -rf {} +
	rm -rf .pytest_cache/
	rm -rf .coverage
	rm -rf htmlcov/
	@echo "$(GREEN)✅ Cleanup complete$(NC)"

clean-data:  ## Clean data and log files (WARNING: deletes all fetched data)
	@echo "$(RED)⚠️  This will delete all data and log files!$(NC)"
	@read -p "Are you sure? (y/N): " confirm && [ "$$confirm" = "y" ] || exit 1
	rm -rf data/ logs/
	@echo "$(GREEN)✅ Data and logs cleaned$(NC)"

clean-all: clean clean-data  ## Clean everything including data (WARNING: destructive)

# ============================================================================
# SETUP AND WORKFLOW TARGETS
# ============================================================================
setup: dev-install pre-commit-install validate-env  ## Complete project setup
	@echo "$(GREEN)🚀 Setup complete!$(NC)"
	@echo ""
	@echo "$(BLUE)Next steps:$(NC)"
	@echo "  1. Configure your .env file if needed"
	@echo "  2. Run: make fetch-data"
	@echo "  3. Check results: make show-data"

dev: format lint  ## Run development checks (format + lint)

ci: format-check lint  ## Run CI checks (no modifications)

fetch-validated: validate-env fetch-data show-data  ## Fetch data with validation and summary

quick-start: setup fetch-validated  ## Complete setup and first data fetch

# ============================================================================
# UTILITY TARGETS
# ============================================================================
deps-update:  ## Update dependencies
	@echo "$(GREEN)🔄 Updating dependencies...$(NC)"
	uv lock --upgrade

info:  ## Show detailed project information
	@echo "$(GREEN)📖 Project Information:$(NC)"
	@echo "$(BLUE)Name:$(NC) Sho Da Igram - Data Engineering"
	@echo "$(BLUE)Purpose:$(NC) Fetch video game data from RAWG API for recommendation system"
	@echo "$(BLUE)Output:$(NC) CSV files optimized for SQL processing"
	@echo "$(BLUE)API:$(NC) RAWG Video Games Database (https://rawg.io/apidocs)"
	@echo ""
	@make status

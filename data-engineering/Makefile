.PHONY: help install lint format clean fetch-rawg fetch-igdb fetch-all show-data setup

# Colors
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
BLUE := \033[0;34m
NC := \033[0m

.DEFAULT_GOAL := help

help:  ## Show available commands
	@echo "$(GREEN)🎮 Sho Da Igram - Data Pipeline$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-15s$(NC) %s\n", $$1, $$2}'

# ============================================================================
# SETUP
# ============================================================================
install:  ## Install dependencies
	@echo "$(GREEN)Installing dependencies...$(NC)"
	uv sync --frozen

setup: install  ## Complete setup
	@echo "$(GREEN)Setting up project...$(NC)"
	@if [ ! -f .env ]; then \
		echo "$(YELLOW)Copying .env.sample to .env...$(NC)"; \
		cp .env.sample .env; \
		echo "$(YELLOW)📝 Edit .env with your API keys$(NC)"; \
	fi
	@echo "$(GREEN)✅ Setup complete!$(NC)"

get-token: ## Generate IGDB access token
	@echo "$(GREEN)Generating IGDB access token...$(NC)"
	uv run python src/sho_da_igram/utils/get_token.py

# ============================================================================
# CODE QUALITY
# ============================================================================
lint:  ## Run code linting
	@echo "$(GREEN)Running linting...$(NC)"
	uv run flake8 src/ main.py

format:  ## Format code
	@echo "$(GREEN)Formatting code...$(NC)"
	uv run isort src/ main.py
	uv run black src/ main.py

check: format lint  ## Format and lint code

# ============================================================================
# DATA FETCHING
# ============================================================================
fetch-rawg:  ## Fetch data from RAWG API only
	@echo "$(GREEN)📥 Fetching RAWG game data...$(NC)"
	uv run python main.py rawg

fetch-igdb:  ## Fetch data from IGDB API only
	@echo "$(GREEN)📥 Fetching IGDB game data...$(NC)"
	uv run python main.py igdb

fetch-all:  ## Fetch data from both APIs
	@echo "$(GREEN)📥 Fetching data from both APIs...$(NC)"
	uv run python main.py both

# Legacy alias
fetch-data: fetch-all  ## Alias for fetch-all

# ============================================================================
# DATA INSPECTION
# ============================================================================
show-data:  ## Show data summary
	@echo "$(GREEN)📊 Data Summary:$(NC)"
	@if [ -d data ] && [ -n "$$(ls -A data 2>/dev/null)" ]; then \
		echo "$(BLUE)Data files:$(NC)"; \
		ls -lah data/; \
		echo ""; \
		echo "$(BLUE)Latest files preview:$(NC)"; \
		for file in $$(ls -t data/*.json 2>/dev/null | head -2); do \
			if command -v jq >/dev/null 2>&1; then \
				echo "📄 $$file"; \
				echo "   Records: $$(jq length "$$file" 2>/dev/null)"; \
				echo "   Sample: $$(jq -r '.[0].name // "N/A"' "$$file" 2>/dev/null)"; \
			else \
				echo "📄 $$file ($$(wc -l < "$$file") lines)"; \
			fi; \
		done; \
	else \
		echo "$(YELLOW)No data files found. Run 'make fetch-all' first.$(NC)"; \
	fi

validate-env:  ## Check environment configuration
	@echo "$(GREEN)🔍 Validating environment...$(NC)"
	@if [ ! -f .env ]; then \
		echo "$(RED)❌ .env file missing$(NC)"; \
		exit 1; \
	fi
	@if grep -q "your_rawg_api_key_here" .env; then \
		echo "$(YELLOW)⚠️  Update RAWG_API_KEY in .env$(NC)"; \
	else \
		echo "$(GREEN)✅ RAWG_API_KEY configured$(NC)"; \
	fi
	@if grep -q "your_twitch_client_id_here" .env; then \
		echo "$(YELLOW)⚠️  Update IGDB credentials in .env$(NC)"; \
	else \
		echo "$(GREEN)✅ IGDB credentials configured$(NC)"; \
	fi

# ============================================================================
# CLEANUP
# ============================================================================
clean:  ## Clean cache files
	@echo "$(GREEN)🧹 Cleaning cache...$(NC)"
	find . -name "__pycache__" -type d -exec rm -rf {} + 2>/dev/null || true
	find . -name "*.pyc" -delete 2>/dev/null || true

clean-data:  ## Delete all data files (WARNING: destructive)
	@echo "$(RED)⚠️  This will delete all fetched data!$(NC)"
	@read -p "Continue? (y/N): " confirm && [ "$$confirm" = "y" ] || exit 1
	rm -rf data/ logs/
	@echo "$(GREEN)✅ Data cleaned$(NC)"

# ============================================================================
# QUICK WORKFLOWS
# ============================================================================
dev: format lint  ## Development workflow (format + lint)

quick-start: setup validate-env fetch-all show-data  ## Complete setup and fetch

test-rawg: validate-env  ## Test RAWG connection
	@echo "$(GREEN)🧪 Testing RAWG connection...$(NC)"
	FETCH_LIMIT=5 uv run python main.py rawg

test-igdb: validate-env  ## Test IGDB connection
	@echo "$(GREEN)🧪 Testing IGDB connection...$(NC)"
	FETCH_LIMIT=5 uv run python main.py igdb

# ============================================================================
# CI/CD
# ============================================================================
ci: format lint
	@echo "$(GREEN)✅ CI checks passed!$(NC)"

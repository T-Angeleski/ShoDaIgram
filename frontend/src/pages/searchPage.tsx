import { useCallback, useEffect, useState } from "react";
import { Box, Chip, Typography } from "@mui/material";

import EmptyState from "../components/common/emptyState";
import ErrorDisplay from "../components/common/errorDisplay";
import LoadingSpinner from "../components/common/loadingSpinner";
import PageContainer from "../components/common/pageContainer";
import Pagination from "../components/common/pagination";
import GameGrid from "../components/game/gameGrid";
import GameImageCarousel from "../components/search/gameImageCarousel";
import SearchBar from "../components/search/searchBar";
import {
  CarouselSection,
  ExampleQueriesContainer,
  SearchContainer,
  SearchHeroSection,
} from "../components/search/styled";
import { useFeaturedGames } from "../hooks/queries/useFeaturedGames";
import { useSearchGames } from "../hooks/queries/useSearchGames";
import { useDebounce } from "../hooks/utils/useDebounce";

const EXAMPLE_QUERIES = [
  "open world RPG",
  "space shooter",
  "puzzle platformer",
  "survival horror",
  "turn-based strategy",
];

const SearchPage = () => {
  const [inputValue, setInputValue] = useState("");
  const [page, setPage] = useState(0);
  const [animatedIndex, setAnimatedIndex] = useState(0);
  const [hasUserTyped, setHasUserTyped] = useState(false);

  const debouncedSearchQuery = useDebounce(inputValue, 300);

  const { data, isLoading, error, refetch } = useSearchGames({
    query: debouncedSearchQuery,
    page,
    size: 20,
  });

  const { data: featuredGames } = useFeaturedGames();

  const showResults = debouncedSearchQuery.length >= 3;
  const hasResults = data && data.games.length > 0;

  useEffect(() => {
    if (hasUserTyped || showResults) return;

    const interval = setInterval(() => {
      setAnimatedIndex((prev) => (prev + 1) % EXAMPLE_QUERIES.length);
    }, 2000);

    return () => clearInterval(interval);
  }, [hasUserTyped, showResults]);

  const currentPlaceholder = hasUserTyped
    ? "Search for games..."
    : `Try: ${EXAMPLE_QUERIES[animatedIndex]}`;

  const handleInputChange = useCallback((query: string) => {
    setInputValue(query);
    setPage(0);
  }, []);

  const handleExampleClick = (example: string) => {
    setHasUserTyped(true);
    setInputValue(example);
    setPage(0);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  return (
    <PageContainer>
      <SearchHeroSection>
        <Typography variant="h3" component="h1" gutterBottom>
          Search Games
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
          Describe the game you're looking for
        </Typography>

        <SearchContainer>
          <SearchBar
            value={inputValue}
            onChange={handleInputChange}
            onUserType={() => setHasUserTyped(true)}
            autoFocus
            placeholder={currentPlaceholder}
          />
        </SearchContainer>

        {!showResults && (
          <>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 3 }}>
              Popular searches:
            </Typography>
            <ExampleQueriesContainer>
              {EXAMPLE_QUERIES.map((example) => (
                <Chip
                  key={example}
                  label={example}
                  onClick={() => handleExampleClick(example)}
                  clickable
                  variant="outlined"
                />
              ))}
            </ExampleQueriesContainer>
          </>
        )}
      </SearchHeroSection>

      {!showResults && featuredGames && featuredGames.games.length > 0 && (
        <CarouselSection>
          <Typography
            variant="h6"
            color="text.secondary"
            textAlign="center"
            sx={{ mb: 4 }}
          >
            Discover Popular Games
          </Typography>
          <GameImageCarousel
            games={featuredGames.games.slice(0, 15)}
            direction="left-to-right"
          />
          <GameImageCarousel
            games={featuredGames.games.slice(15, 30)}
            direction="right-to-left"
          />
        </CarouselSection>
      )}

      {showResults && (
        <>
          {isLoading && <LoadingSpinner message="Searching..." />}

          {error && <ErrorDisplay error={error} onRetry={refetch} />}

          {!isLoading && !error && !hasResults && (
            <EmptyState
              message={`No games found for "${debouncedSearchQuery}"`}
              actionLabel="Clear Search"
              onAction={() => {
                setInputValue("");
                setHasUserTyped(false);
              }}
            />
          )}

          {!isLoading && !error && hasResults && (
            <>
              <Box sx={{ mb: 3 }}>
                <Typography variant="h5" gutterBottom>
                  Search Results
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Found {data.totalResults.toLocaleString()} games matching "
                  {debouncedSearchQuery}"
                </Typography>
              </Box>

              <GameGrid games={data.games} />

              {data.totalPages > 1 && (
                <Pagination
                  currentPage={page}
                  totalPages={data.totalPages}
                  onPageChange={handlePageChange}
                  totalResults={data.totalResults}
                />
              )}
            </>
          )}
        </>
      )}
    </PageContainer>
  );
};

export default SearchPage;

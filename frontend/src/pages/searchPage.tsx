import { useCallback, useEffect, useState } from "react";
import { Box, Chip, Typography } from "@mui/material";

import EmptyState from "../components/common/emptyState";
import ErrorDisplay from "../components/common/errorDisplay";
import LoadingSpinner from "../components/common/loadingSpinner";
import PageContainer from "../components/common/pageContainer";
import Pagination from "../components/common/pagination";
import GameGrid from "../components/game/gameGrid";
import SearchBar from "../components/search/searchBar";
import {
  ExampleQueriesContainer,
  SearchContainer,
  SearchHeroSection,
} from "../components/search/styled";
import { useSearchGames } from "../hooks/queries/useSearchGames";

const EXAMPLE_QUERIES = [
  "open world RPG",
  "space shooter",
  "puzzle platformer",
  "survival horror",
  "turn-based strategy",
];

const SearchPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [page, setPage] = useState(0);
  const [animatedIndex, setAnimatedIndex] = useState(0);
  const [hasUserTyped, setHasUserTyped] = useState(false);

  const { data, isLoading, error, refetch } = useSearchGames({
    query: searchQuery,
    page,
    size: 20,
  });

  const showResults = searchQuery.length >= 3;
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

  const handleSearch = useCallback((query: string) => {
    setSearchQuery(query);
    setPage(0);
  }, []);

  const handleExampleClick = (example: string) => {
    setHasUserTyped(true);
    setSearchQuery(example);
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
            onSearch={handleSearch}
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

      {showResults && (
        <>
          {isLoading && <LoadingSpinner message="Searching..." />}

          {error && <ErrorDisplay error={error} onRetry={refetch} />}

          {!isLoading && !error && !hasResults && (
            <EmptyState
              message={`No games found for "${searchQuery}"`}
              actionLabel="Clear Search"
              onAction={() => {
                setSearchQuery("");
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
                  {searchQuery}"
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

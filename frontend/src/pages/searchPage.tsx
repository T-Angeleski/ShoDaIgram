import { useCallback, useEffect, useState } from "react";
import {
  Box,
  Chip,
  Fade,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Typography,
} from "@mui/material";

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
  const [sortParam, setSortParam] = useState("relevance,desc");

  const { data, isLoading, error, refetch } = useSearchGames({
    query: searchQuery,
    page,
    size: 20,
    sort: sortParam === "relevance,desc" ? undefined : sortParam,
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
    handleSearch(example);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const handleSortChange = (event: SelectChangeEvent) => {
    setSortParam(event.target.value);
    setPage(0);
  };

  return (
    <PageContainer>
      <Fade in={true} timeout={500}>
        <div>
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
                initialValue={searchQuery}
              />
            </SearchContainer>

            {!showResults && (
              <>
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mt: 3 }}
                >
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
                  <Box
                    sx={{
                      mb: 3,
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      flexWrap: "wrap",
                      gap: 2,
                    }}
                  >
                    <Box>
                      <Typography variant="h5" gutterBottom>
                        Search Results
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Found {data.totalResults.toLocaleString()} games
                        matching "{searchQuery}"
                      </Typography>
                    </Box>

                    <FormControl size="small" sx={{ minWidth: 200 }}>
                      <InputLabel id="search-sort-label">Sort By</InputLabel>
                      <Select
                        labelId="search-sort-label"
                        id="search-sort"
                        value={sortParam}
                        label="Sort By"
                        onChange={handleSortChange}
                      >
                        <MenuItem value="relevance,desc">Relevance</MenuItem>
                        <MenuItem value="ratingCount,desc">
                          Popularity (Most Ratings)
                        </MenuItem>
                        <MenuItem value="rating,desc">
                          Rating (High to Low)
                        </MenuItem>
                        <MenuItem value="rating,asc">
                          Rating (Low to High)
                        </MenuItem>
                        <MenuItem value="releaseDate,desc">
                          Release Date (Newest)
                        </MenuItem>
                        <MenuItem value="name,asc">Name (A-Z)</MenuItem>
                      </Select>
                    </FormControl>
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
        </div>
      </Fade>
    </PageContainer>
  );
};

export default SearchPage;

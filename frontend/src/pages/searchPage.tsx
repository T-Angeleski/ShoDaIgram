import { useCallback, useEffect, useState } from "react";
import {
  Chip,
  Fade,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Typography,
} from "@mui/material";

import ErrorDisplay from "../components/common/errorDisplay";
import LoadingSpinner from "../components/common/loadingSpinner";
import PageContainer from "../components/common/pageContainer";
import Pagination from "../components/common/pagination";
import GameGrid from "../components/game/gameGrid";
import SearchBar from "../components/search/searchBar";
import {
  ClearLink,
  EmptyStateActions,
  EmptyStateContainer,
  EmptyStateDescription,
  ExampleQueriesContainer,
  HeroDescription,
  SearchContainer,
  SearchHeaderContainer,
  SearchHeroSection,
  SearchResultsHeader,
} from "../components/search/styled";
import { useSearchGames } from "../hooks/queries/useSearchGames";
import { useRecentSearches } from "../hooks/useRecentSearches";

const EXAMPLE_QUERIES = [
  "open world RPG",
  "space shooter",
  "puzzle platformer",
  "survival horror",
  "turn-based strategy",
];

const SEARCH_SUGGESTIONS = [
  "shooter",
  "rpg",
  "strategy",
  "open world",
  "puzzle",
  "multiplayer",
  "adventure",
  "action",
];

const SearchPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [page, setPage] = useState(0);
  const [animatedIndex, setAnimatedIndex] = useState(0);
  const [hasUserTyped, setHasUserTyped] = useState(false);
  const [sortParam, setSortParam] = useState("relevance,desc");
  const { recentSearches, addSearch, clearSearches } = useRecentSearches();

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

  const handleSearch = useCallback(
    (query: string) => {
      setSearchQuery(query);
      setPage(0);
      if (query.trim().length >= 3) {
        addSearch(query);
      }
    },
    [addSearch],
  );

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
            <HeroDescription variant="body1" color="text.secondary">
              Describe the game you're looking for
            </HeroDescription>

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
                <SearchHeaderContainer>
                  <Typography variant="body2" color="text.secondary">
                    {recentSearches.length > 0
                      ? "Recent & popular searches:"
                      : "Popular searches:"}
                  </Typography>
                  {recentSearches.length > 0 && (
                    <ClearLink
                      variant="caption"
                      color="primary"
                      onClick={clearSearches}
                    >
                      Clear recent
                    </ClearLink>
                  )}
                </SearchHeaderContainer>
                <ExampleQueriesContainer>
                  {recentSearches.map((search) => (
                    <Chip
                      key={`recent-${search}`}
                      label={search}
                      onClick={() => handleExampleClick(search)}
                      clickable
                      variant="filled"
                      color="primary"
                      size="small"
                    />
                  ))}
                  {EXAMPLE_QUERIES.filter(
                    (example) =>
                      !recentSearches.some(
                        (recent) =>
                          recent.toLowerCase() === example.toLowerCase(),
                      ),
                  ).map((example) => (
                    <Chip
                      key={`popular-${example}`}
                      label={example}
                      onClick={() => handleExampleClick(example)}
                      clickable
                      variant="outlined"
                      size="small"
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
                <EmptyStateContainer>
                  <Typography variant="h5" gutterBottom>
                    No games found for "{searchQuery}"
                  </Typography>
                  <EmptyStateDescription variant="body2" color="text.secondary">
                    Try different keywords or explore these suggestions:
                  </EmptyStateDescription>
                  <ExampleQueriesContainer>
                    {SEARCH_SUGGESTIONS.map((suggestion) => (
                      <Chip
                        key={suggestion}
                        label={suggestion}
                        onClick={() => handleExampleClick(suggestion)}
                        clickable
                        variant="outlined"
                        color="primary"
                      />
                    ))}
                  </ExampleQueriesContainer>
                  <EmptyStateActions>
                    <Chip
                      label="Clear Search"
                      onClick={() => {
                        setSearchQuery("");
                        setHasUserTyped(false);
                      }}
                      clickable
                      variant="filled"
                    />
                  </EmptyStateActions>
                </EmptyStateContainer>
              )}

              {!isLoading && !error && hasResults && (
                <>
                  <SearchResultsHeader>
                    <div>
                      <Typography variant="h5" gutterBottom>
                        Search Results
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Found {data.totalResults.toLocaleString()} games
                        matching "{searchQuery}"
                      </Typography>
                    </div>

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
                  </SearchResultsHeader>

                  <GameGrid games={data.games} searchQuery={searchQuery} />

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

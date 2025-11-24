import { useMemo } from "react";
import { useSearchParams } from "react-router-dom";
import {
  Fade,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
} from "@mui/material";

import EmptyState from "../components/common/emptyState";
import PageContainer from "../components/common/pageContainer";
import Pagination from "../components/common/pagination";
import GameGrid from "../components/game/gameGrid";
import TagFilter from "../components/game/tagFilter";
import { useGames } from "../hooks/queries/useGames";
import { useTags } from "../hooks/queries/useTags";

import { PageDescription, PageHeader, PageTitle } from "./styled";

const GameBrowserPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { data: tagsResponse } = useTags();

  const page = Number.parseInt(searchParams.get("page") ?? "0");
  const tagParams = searchParams.get("tags");
  const sortParam = searchParams.get("sort") ?? "ratingCount,desc";

  const selectedTagNames = useMemo(
    () => (tagParams ? tagParams.split(",") : []),
    [tagParams],
  );

  const selectedTagIds = useMemo(() => {
    if (selectedTagNames.length === 0 || !tagsResponse) return [];

    return selectedTagNames
      .map(
        (name) => tagsResponse.tags.find((t) => t.normalizedName === name)?.id,
      )
      .filter((id): id is number => id !== undefined);
  }, [selectedTagNames, tagsResponse]);

  const { data, isLoading, error } = useGames({
    page,
    size: 20,
    tags: selectedTagNames.length > 0 ? selectedTagNames : undefined,
    sort: sortParam,
  });

  const handleTagsChange = (tagIds: number[]) => {
    const newParams = new URLSearchParams(searchParams);
    if (tagIds.length > 0 && tagsResponse) {
      const normalizedNames = tagIds
        .map((id) => tagsResponse.tags.find((t) => t.id === id)?.normalizedName)
        .filter((name): name is string => name !== undefined);
      newParams.set("tags", normalizedNames.join(","));
    } else {
      newParams.delete("tags");
    }
    newParams.set("page", "0");
    setSearchParams(newParams);
  };

  const handlePageChange = (newPage: number) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set("page", newPage.toString());
    setSearchParams(newParams);
  };

  const handleSortChange = (event: SelectChangeEvent) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set("sort", event.target.value);
    newParams.set("page", "0");
    setSearchParams(newParams);
  };

  const handleClearFilters = () => {
    setSearchParams(new URLSearchParams());
  };

  return (
    <PageContainer>
      <Fade in={true} timeout={500}>
        <div>
          <PageHeader>
            <PageTitle>Browse Games</PageTitle>
            <PageDescription>
              Explore our collection of{" "}
              {data?.totalResults.toLocaleString() ?? "thousands of"} games
            </PageDescription>
          </PageHeader>

          <FormControl
            size="small"
            sx={{ minWidth: 200, marginBottom: (theme) => theme.spacing(2) }}
          >
            <InputLabel id="sort-select-label">Sort By</InputLabel>
            <Select
              labelId="sort-select-label"
              id="sort-select"
              value={sortParam}
              label="Sort By"
              onChange={handleSortChange}
              disabled={isLoading}
            >
              <MenuItem value="ratingCount,desc">
                Popularity (Most Ratings)
              </MenuItem>
              <MenuItem value="rating,desc">Rating (High to Low)</MenuItem>
              <MenuItem value="rating,asc">Rating (Low to High)</MenuItem>
              <MenuItem value="releaseDate,desc">
                Release Date (Newest)
              </MenuItem>
              <MenuItem value="releaseDate,asc">Release Date (Oldest)</MenuItem>
              <MenuItem value="name,asc">Name (A-Z)</MenuItem>
              <MenuItem value="name,desc">Name (Z-A)</MenuItem>
            </Select>
          </FormControl>

          <TagFilter
            selectedTagIds={selectedTagIds}
            onTagsChange={handleTagsChange}
            disabled={isLoading}
          />

          {!isLoading &&
          data?.games.length === 0 &&
          selectedTagIds.length > 0 ? (
            <EmptyState
              message="No games found with selected tags"
              actionLabel="Clear Filters"
              onAction={handleClearFilters}
            />
          ) : (
            <GameGrid
              games={data?.games ?? []}
              isLoading={isLoading}
              isEmpty={!!error || (data?.games.length === 0 && !isLoading)}
            />
          )}

          {data && data.totalPages > 1 && (
            <Pagination
              currentPage={page}
              totalPages={data.totalPages}
              onPageChange={handlePageChange}
              totalResults={data.totalResults}
            />
          )}
        </div>
      </Fade>
    </PageContainer>
  );
};

export default GameBrowserPage;

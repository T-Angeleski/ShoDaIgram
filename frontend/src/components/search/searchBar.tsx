import { ChangeEvent, useEffect, useState } from "react";
import { Clear as ClearIcon, Search as SearchIcon } from "@mui/icons-material";
import {
  CircularProgress,
  IconButton,
  InputAdornment,
  TextField,
} from "@mui/material";

import { useDebounce } from "../../hooks/utils/useDebounce";

interface SearchBarProps {
  onSearch: (query: string) => void;
  onUserType?: () => void;
  placeholder?: string;
  autoFocus?: boolean;
}

const SearchBar = ({
  onSearch,
  onUserType,
  placeholder = "Search for games...",
  autoFocus = false,
}: SearchBarProps) => {
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    onSearch(debouncedQuery);
  }, [debouncedQuery, onSearch]);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setQuery(e.target.value);
    if (e.target.value.length > 0) {
      onUserType?.();
    }
  };

  const handleClear = () => {
    setQuery("");
  };

  const isSearching = query.length > 0 && query !== debouncedQuery;

  return (
    <TextField
      fullWidth
      value={query}
      onChange={handleChange}
      placeholder={placeholder}
      autoFocus={autoFocus}
      slotProps={{
        input: {
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon color="action" />
            </InputAdornment>
          ),
          endAdornment: query.length > 0 && (
            <InputAdornment position="end">
              {isSearching ? (
                <CircularProgress size={20} />
              ) : (
                <IconButton
                  size="small"
                  onClick={handleClear}
                  aria-label="Clear search"
                >
                  <ClearIcon />
                </IconButton>
              )}
            </InputAdornment>
          ),
        },
      }}
    />
  );
};

export default SearchBar;

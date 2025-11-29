import { ChangeEvent } from "react";
import { Clear as ClearIcon, Search as SearchIcon } from "@mui/icons-material";
import { IconButton, InputAdornment, TextField } from "@mui/material";

interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  onUserType?: () => void;
  placeholder?: string;
  autoFocus?: boolean;
}

const SearchBar = ({
  value,
  onChange,
  onUserType,
  placeholder = "Search for games...",
  autoFocus = false,
}: SearchBarProps) => {
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    onChange(e.target.value);
    if (e.target.value.length > 0) {
      onUserType?.();
    }
  };

  const handleClear = () => {
    onChange("");
  };

  return (
    <TextField
      fullWidth
      value={value}
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
          endAdornment: value.length > 0 && (
            <InputAdornment position="end">
              <IconButton
                size="small"
                onClick={handleClear}
                aria-label="Clear search"
              >
                <ClearIcon />
              </IconButton>
            </InputAdornment>
          ),
        },
      }}
    />
  );
};

export default SearchBar;

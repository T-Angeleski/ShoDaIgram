import { useMemo } from "react";
import { Autocomplete, TextField } from "@mui/material";

import { useTags } from "../../hooks/queries/useTags";
import { TagDto } from "../../types/game.types";

import { TagFilterContainer } from "./styled";

interface TagFilterProps {
  selectedTagIds: number[];
  onTagsChange: (tagIds: number[]) => void;
  disabled?: boolean;
}

const TagFilter = ({
  selectedTagIds,
  onTagsChange,
  disabled = false,
}: TagFilterProps) => {
  const { data: tagsResponse, isLoading } = useTags();

  const handleChange = (_event: unknown, value: TagDto[]) => {
    onTagsChange(value.map((tag) => tag.id));
  };

  const sortedTags = useMemo(() => {
    const tags = tagsResponse?.tags ?? [];
    return [...tags].sort((a, b) => {
      const categoryCompare = a.category.localeCompare(b.category);
      if (categoryCompare !== 0) return categoryCompare;
      return a.name.localeCompare(b.name);
    });
  }, [tagsResponse?.tags]);

  const selectedTagObjects = sortedTags.filter((tag) =>
    selectedTagIds.includes(tag.id),
  );

  return (
    <TagFilterContainer>
      <Autocomplete
        multiple
        options={sortedTags}
        value={selectedTagObjects}
        onChange={handleChange}
        getOptionLabel={(option) => option.name}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        groupBy={(option) => option.category}
        disabled={disabled || isLoading}
        loading={isLoading}
        renderInput={(params) => (
          <TextField
            {...params}
            label="Filter by Tags"
            placeholder="Select tags..."
            variant="outlined"
          />
        )}
        slotProps={{
          chip: {
            size: "small",
          },
        }}
      />
    </TagFilterContainer>
  );
};

export default TagFilter;

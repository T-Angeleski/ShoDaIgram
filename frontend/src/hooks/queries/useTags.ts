import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { tagService } from "../../services/tagService";
import { TagCategory, TagListParams } from "../../types/api.types";
import { TagListResponse, TagSummaryDto } from "../../types/game.types";
import { TEN_MINUTES } from "../../utils/appConstants";

/**
 * Hook to fetch all tags with pagination
 */
export const useTags = (
  params: TagListParams = {},
): UseQueryResult<TagListResponse, Error> => {
  return useQuery({
    queryKey: ["tags", params],
    queryFn: () => tagService.getTags(params),
    staleTime: TEN_MINUTES,
  });
};

/**
 * Hook to search tags by name
 */
export const useSearchTags = (
  search: string,
  limit?: number,
): UseQueryResult<TagSummaryDto[], Error> => {
  return useQuery({
    queryKey: ["tags", "search", search, limit],
    queryFn: () => tagService.searchTags(search, limit),
    enabled: search.length >= 2,
    staleTime: TEN_MINUTES,
  });
};

/**
 * Hook to get tags by category
 */
export const useTagsByCategory = (
  category: TagCategory,
  params: TagListParams = {},
): UseQueryResult<TagListResponse, Error> => {
  return useQuery({
    queryKey: ["tags", "category", category, params],
    queryFn: () => tagService.getTagsByCategory(category, params),
    staleTime: TEN_MINUTES,
  });
};

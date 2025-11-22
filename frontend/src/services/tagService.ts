import { TagCategory, TagListParams } from "../types/api.types";
import {
  TagGamesResponseDto,
  TagListResponse,
  TagSummaryDto,
} from "../types/game.types";

import { apiClient } from "./api";

export const tagService = {
  getTags: async (params: TagListParams = {}): Promise<TagListResponse> => {
    const response = await apiClient.get<TagListResponse>("/tags", {
      params: {
        category: params.category,
        search: params.search,
        page: params.page ?? 0,
        size: params.size ?? 100,
        limit: params.limit,
      },
    });
    return response.data;
  },

  searchTags: async (
    search: string,
    limit?: number,
  ): Promise<TagSummaryDto[]> => {
    const response = await apiClient.get<TagSummaryDto[]>("/tags", {
      params: { search, limit },
    });
    return response.data;
  },

  getTagsByCategory: async (
    category: TagCategory,
    params: TagListParams = {},
  ): Promise<TagListResponse> => {
    const response = await apiClient.get<TagListResponse>("/tags", {
      params: {
        category,
        page: params.page ?? 0,
        size: params.size ?? 100,
      },
    });
    return response.data;
  },

  getGamesByTag: async (
    tagName: string,
    page: number = 0,
    size: number = 20,
  ): Promise<TagGamesResponseDto> => {
    const response = await apiClient.get<TagGamesResponseDto>(
      `/tags/${tagName}/games`,
      {
        params: { page, size },
      },
    );
    return response.data;
  },
};

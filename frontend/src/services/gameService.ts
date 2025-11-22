import { GameListParams, SearchParams } from "../types/api.types";
import {
  GameDetailDto,
  GamePageDto,
  SearchResultDto,
} from "../types/game.types";
import {
  GameFilterResponse,
  RecommendationResponse,
} from "../types/recommendation.types";

import { apiClient } from "./api";

export const gameService = {
  getGames: async (params: GameListParams = {}): Promise<GamePageDto> => {
    if (params.tags && params.tags.length > 0) {
      const filterResponse = await apiClient.get<GameFilterResponse>(
        "/games/filter",
        {
          params: {
            tags: params.tags.join(","),
            page: params.page ?? 0,
            size: params.size ?? 20,
            sort: params.sort ?? "rating,desc",
          },
        },
      );

      return {
        games: filterResponse.data.games,
        page: filterResponse.data.page,
        pageSize: filterResponse.data.pageSize,
        totalResults: filterResponse.data.totalResults,
        totalPages: filterResponse.data.totalPages,
        isFirst: filterResponse.data.isFirst,
        isLast: filterResponse.data.isLast,
      };
    }

    const response = await apiClient.get<GamePageDto>("/games", {
      params: {
        page: params.page ?? 0,
        size: params.size ?? 20,
        sort: params.sort ?? "rating,desc",
      },
    });
    return response.data;
  },

  getGameById: async (id: number): Promise<GameDetailDto> => {
    const response = await apiClient.get<GameDetailDto>(`/games/${id}`);
    return response.data;
  },

  getGameBySlug: async (slug: string): Promise<GameDetailDto> => {
    const response = await apiClient.get<GameDetailDto>(`/games/slug/${slug}`);
    return response.data;
  },

  searchGames: async (params: SearchParams): Promise<SearchResultDto> => {
    const response = await apiClient.get<SearchResultDto>("/games/search", {
      params: {
        query: params.query,
        page: params.page ?? 0,
        size: params.size ?? 20,
      },
    });
    return response.data;
  },

  getSimilarGames: async (
    gameId: number,
    limit: number = 10,
    explainability: boolean = false,
  ): Promise<RecommendationResponse> => {
    const response = await apiClient.get<RecommendationResponse>(
      `/games/${gameId}/recommendations`,
      {
        params: { limit, explainability },
      },
    );
    return response.data;
  },

  filterGames: async (params: GameListParams): Promise<GameFilterResponse> => {
    const response = await apiClient.get<GameFilterResponse>("/games/filter", {
      params: {
        tags: params.tags?.join(","),
        minRating: params.minRating,
        maxRating: params.maxRating,
        minYear: params.minYear,
        maxYear: params.maxYear,
        page: params.page ?? 0,
        size: params.size ?? 20,
        sort: params.sort ?? "rating,desc",
      },
    });
    return response.data;
  },

  getGamesByTags: async (
    tags: string[],
    params: GameListParams = {},
  ): Promise<GameFilterResponse> => {
    const response = await apiClient.get<GameFilterResponse>("/games/by-tags", {
      params: {
        tags: tags.join(","),
        page: params.page ?? 0,
        size: params.size ?? 20,
        sort: params.sort ?? "rating,desc",
      },
    });
    return response.data;
  },
};

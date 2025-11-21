import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { gameService } from "../../services/gameService";
import { GameDetailDto } from "../../types/game.types";
import { TEN_MINUTES } from "../../utils/appConstants";

/**
 * Hook to fetch single game by ID
 */
export const useGame = (
  id: number | undefined,
): UseQueryResult<GameDetailDto, Error> => {
  return useQuery({
    queryKey: ["game", id],
    queryFn: () => gameService.getGameById(id!),
    enabled: id !== undefined,
    staleTime: TEN_MINUTES,
  });
};

/**
 * Hook to fetch single game by slug
 */
export const useGameBySlug = (
  slug: string | undefined,
): UseQueryResult<GameDetailDto, Error> => {
  return useQuery({
    queryKey: ["game", "slug", slug],
    queryFn: () => gameService.getGameBySlug(slug!),
    enabled: slug !== undefined && slug.length > 0,
    staleTime: TEN_MINUTES,
  });
};

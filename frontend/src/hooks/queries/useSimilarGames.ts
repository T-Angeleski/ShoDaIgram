import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { gameService } from "../../services/gameService";
import { RecommendationResponse } from "../../types/recommendation.types";
import { FIVE_MINUTES } from "../../utils/appConstants";

/**
 * Hook to fetch similar games recommendations
 */
export const useSimilarGames = (
  gameId: number | undefined,
  limit: number = 10,
  explainability: boolean = false,
): UseQueryResult<RecommendationResponse, Error> => {
  return useQuery({
    queryKey: ["similar-games", gameId, limit, explainability],
    queryFn: () => gameService.getSimilarGames(gameId!, limit, explainability),
    enabled: gameId !== undefined,
    staleTime: FIVE_MINUTES,
  });
};

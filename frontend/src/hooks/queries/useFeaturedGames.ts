import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { gameService } from "../../services/gameService";
import { GamePageDto } from "../../types/game.types";
import { FIVE_MINUTES } from "../../utils/appConstants";

/**
 * Hook to fetch featured games for carousel display
 * Fetches top-rated games with images
 */
export const useFeaturedGames = (): UseQueryResult<GamePageDto, Error> => {
  return useQuery({
    queryKey: ["featured-games"],
    queryFn: () =>
      gameService.getGames({
        page: 0,
        size: 30,
        sort: "ratingCount,desc",
      }),
    staleTime: FIVE_MINUTES,
  });
};

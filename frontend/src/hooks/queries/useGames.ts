import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { gameService } from "../../services/gameService";
import { GameListParams } from "../../types/api.types";
import { GamePageDto } from "../../types/game.types";
import { FIVE_MINUTES } from "../../utils/appConstants";

/**
 * Hook to fetch paginated list of games
 */
export const useGames = (
  params: GameListParams = {},
): UseQueryResult<GamePageDto, Error> => {
  return useQuery({
    queryKey: ["games", params],
    queryFn: () => gameService.getGames(params),
    staleTime: FIVE_MINUTES,
  });
};

import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { gameService } from "../../services/gameService";
import { SearchParams } from "../../types/api.types";
import { SearchResultDto } from "../../types/game.types";
import { FIVE_MINUTES } from "../../utils/appConstants";

/**
 * Hook to search games with natural language query
 */
export const useSearchGames = (
  params: SearchParams,
): UseQueryResult<SearchResultDto, Error> => {
  return useQuery({
    queryKey: ["search-games", params],
    queryFn: () => gameService.searchGames(params),
    enabled: params.query.length >= 3,
    staleTime: FIVE_MINUTES,
  });
};

import { Skeleton } from "@mui/material";

import { GameSearchDto, GameSummaryDto } from "../../types/game.types";
import { SimilarGameWithReasonsDto } from "../../types/recommendation.types";
import EmptyState from "../common/emptyState";

import GameCard from "./gameCard";
import { GameCardContent, GameGridContainer, StyledGameCard } from "./styled";

type GameType = GameSummaryDto | SimilarGameWithReasonsDto | GameSearchDto;
interface GameGridProps {
  games: GameType[];
  isLoading?: boolean;
  isEmpty?: boolean;
  variant?: "default" | "similar";
  searchQuery?: string;
}

const GameGridSkeleton = () => (
  <GameGridContainer>
    {Array.from({ length: 12 }).map((_, index) => (
      <StyledGameCard key={index * 2}>
        <Skeleton
          variant="rectangular"
          width="100%"
          height={180}
          animation="wave"
          sx={{ bgcolor: "grey.200" }}
        />
        <GameCardContent>
          <Skeleton variant="text" width="80%" height={32} animation="wave" />
          <Skeleton variant="text" width="60%" height={24} animation="wave" />
        </GameCardContent>
      </StyledGameCard>
    ))}
  </GameGridContainer>
);

const GameGrid = ({
  games,
  isLoading = false,
  isEmpty = false,
  variant = "default",
  searchQuery,
}: GameGridProps) => {
  if (isLoading) {
    return <GameGridSkeleton />;
  }

  if (isEmpty || games.length === 0) {
    return <EmptyState message="No games found" />;
  }

  const getGameKey = (game: GameType) => {
    return "gameId" in game ? game.gameId : game.id;
  };

  return (
    <GameGridContainer>
      {games.map((game) => (
        <GameCard
          key={getGameKey(game)}
          game={game}
          variant={variant}
          searchQuery={searchQuery}
        />
      ))}
    </GameGridContainer>
  );
};

export default GameGrid;

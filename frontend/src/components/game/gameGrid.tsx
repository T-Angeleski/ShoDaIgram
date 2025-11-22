import { Skeleton } from "@mui/material";

import { GameSummaryDto } from "../../types/game.types";
import { SimilarGameWithReasonsDto } from "../../types/recommendation.types";
import EmptyState from "../common/emptyState";

import GameCard from "./gameCard";
import { GameCardContent, GameGridContainer, StyledGameCard } from "./styled";

interface GameGridProps {
  games: (GameSummaryDto | SimilarGameWithReasonsDto)[];
  isLoading?: boolean;
  isEmpty?: boolean;
  variant?: "default" | "similar";
}

const GameGridSkeleton = () => (
  <GameGridContainer>
    {Array.from({ length: 12 }).map((_, index) => (
      <StyledGameCard key={index * 2}>
        <Skeleton variant="rectangular" width="100%" height={180} />
        <GameCardContent>
          <Skeleton variant="text" width="80%" height={32} />
          <Skeleton variant="text" width="60%" height={24} />
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
}: GameGridProps) => {
  if (isLoading) {
    return <GameGridSkeleton />;
  }

  if (isEmpty || games.length === 0) {
    return <EmptyState message="No games found" />;
  }

  const getGameKey = (game: GameSummaryDto | SimilarGameWithReasonsDto) => {
    return "gameId" in game ? game.gameId : game.id;
  };

  return (
    <GameGridContainer>
      {games.map((game) => (
        <GameCard key={getGameKey(game)} game={game} variant={variant} />
      ))}
    </GameGridContainer>
  );
};

export default GameGrid;

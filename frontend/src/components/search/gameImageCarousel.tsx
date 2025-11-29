import { useNavigate } from "react-router-dom";

import { GameSummaryDto } from "../../types/game.types";

import {
  CarouselContainer,
  CarouselTrack,
  GameImageCard,
  GameImageOverlay,
  GameTitle,
} from "./styled";

interface GameImageCarouselProps {
  games: GameSummaryDto[];
  direction?: "left-to-right" | "right-to-left";
}

const GameImageCarousel = ({
  games,
  direction = "left-to-right",
}: GameImageCarouselProps) => {
  const navigate = useNavigate();

  if (!games || games.length === 0) return null;

  // Duplicate games for seamless infinite scroll
  const duplicatedGames = [...games, ...games, ...games];

  const handleGameClick = (gameId: number) => {
    navigate(`/games/${gameId}`);
  };

  return (
    <CarouselContainer>
      <CarouselTrack direction={direction}>
        {duplicatedGames.map((game, index) => (
          <GameImageCard
            key={`${game.id}-${index}`}
            onClick={() => handleGameClick(game.id)}
            $hasImage={!!game.backgroundImageUrl}
          >
            {game.backgroundImageUrl && (
              <img
                src={game.backgroundImageUrl}
                alt={game.name}
                loading="lazy"
              />
            )}
            <GameImageOverlay>
              <GameTitle variant="body2">{game.name}</GameTitle>
            </GameImageOverlay>
          </GameImageCard>
        ))}
      </CarouselTrack>
    </CarouselContainer>
  );
};

export default GameImageCarousel;

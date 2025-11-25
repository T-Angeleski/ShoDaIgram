import { memo } from "react";
import { useNavigate } from "react-router-dom";
import { Box, CardContent, Chip, Rating } from "@mui/material";

import { GameSearchDto, GameSummaryDto } from "../../types/game.types";
import { SimilarGameWithReasonsDto } from "../../types/recommendation.types";
import HighlightedText from "../common/highlightedText";

import MatchReasonBadge from "./matchReasonBadge";
import {
  GameCardContent,
  GameCardImage,
  GameDescription,
  GameMetadata,
  GameTitle,
  MatchReasonsContainer,
  SimilarGameCardWrapper,
  SimilarityBadge,
  StyledGameCard,
} from "./styled";

type GameCardData = GameSummaryDto | SimilarGameWithReasonsDto | GameSearchDto;

interface GameCardProps {
  game: GameCardData;
  variant?: "default" | "similar";
  searchQuery?: string;
}

const isSimilarGame = (
  game: GameCardData,
): game is SimilarGameWithReasonsDto => {
  return "similarityScore" in game;
};

const isGameSearch = (game: GameCardData): game is GameSearchDto => {
  return "description" in game;
};

const getGradientColors = (id: number): [string, string] => {
  const colors: [string, string][] = [
    ["#3b82f6", "#2563eb"],
    ["#8b5cf6", "#7c3aed"],
    ["#ec4899", "#db2777"],
    ["#f59e0b", "#d97706"],
    ["#10b981", "#059669"],
    ["#06b6d4", "#0891b2"],
  ];
  return colors[id % colors.length] ?? ["#64748b", "#475569"];
};

const GameCard = ({
  game,
  variant = "default",
  searchQuery,
}: GameCardProps) => {
  const navigate = useNavigate();

  const gameId = isSimilarGame(game) ? game.gameId : game.id;
  const { name, rating, releaseDate, backgroundImageUrl } = game;

  const handleClick = () => {
    navigate(`/games/${gameId}`);
  };

  const getRatingValue = (rating: number | null) => {
    return rating ? rating / 2 : 0;
  };

  const getYear = (dateStr: string | null) => {
    return dateStr ? dateStr.split("-")[0] : null;
  };

  const [color1, color2] = getGradientColors(gameId);

  const CardWrapper =
    variant === "similar" ? SimilarGameCardWrapper : StyledGameCard;

  return (
    <CardWrapper onClick={handleClick}>
      {variant === "similar" && isSimilarGame(game) && (
        <SimilarityBadge score={game.similarityScore}>
          {Math.round(game.similarityScore * 100)}%
        </SimilarityBadge>
      )}
      {backgroundImageUrl ? (
        <GameCardImage
          src={backgroundImageUrl}
          alt={`Cover art for ${name}`}
          loading="lazy"
          onError={(e) => {
            const target = e.currentTarget as HTMLImageElement;
            target.style.display = "none";
          }}
        />
      ) : (
        <Box
          sx={{
            width: "100%",
            aspectRatio: "16/9",
            background: `linear-gradient(135deg, ${color1} 0%, ${color2} 100%)`,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: "4rem",
            fontWeight: 700,
            color: "white",
            textTransform: "uppercase",
          }}
        >
          {name.charAt(0)}
        </Box>
      )}
      {variant === "similar" ? (
        <CardContent>
          <GameTitle>{name}</GameTitle>
          <GameMetadata>
            <Rating
              value={getRatingValue(rating)}
              readOnly
              precision={0.5}
              size="small"
            />
            {getYear(releaseDate) && (
              <Chip label={getYear(releaseDate)} size="small" />
            )}
          </GameMetadata>
          {isSimilarGame(game) && game.matchReasons.length > 0 && (
            <MatchReasonsContainer>
              {game.matchReasons.map((reason, index) => (
                <MatchReasonBadge
                  key={`${reason.type}-${reason.contribution}-${index}`}
                  reason={reason}
                />
              ))}
            </MatchReasonsContainer>
          )}
        </CardContent>
      ) : (
        <GameCardContent>
          <GameTitle>{name}</GameTitle>
          <GameMetadata>
            <Rating
              value={getRatingValue(rating)}
              readOnly
              precision={0.5}
              size="small"
            />
            {getYear(releaseDate) && (
              <Chip label={getYear(releaseDate)} size="small" />
            )}
          </GameMetadata>
          {isGameSearch(game) && game.description && (
            <GameDescription>
              {searchQuery ? (
                <HighlightedText text={game.description} query={searchQuery} />
              ) : (
                game.description
              )}
            </GameDescription>
          )}
        </GameCardContent>
      )}
    </CardWrapper>
  );
};

export default memo(GameCard);

import { useNavigate, useParams } from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { Button, Chip, Rating, Typography } from "@mui/material";

import ErrorDisplay from "../components/common/errorDisplay";
import LoadingSpinner from "../components/common/loadingSpinner";
import PageContainer from "../components/common/pageContainer";
import GameGrid from "../components/game/gameGrid";
import { BackButtonContainer, HeroSection } from "../components/game/styled";
import { useGame } from "../hooks/queries/useGame";
import { useSimilarGames } from "../hooks/queries/useSimilarGames";

import {
  Divider,
  GameImage,
  GameMetadata,
  GameTitle,
  RatingContainer,
  SectionContainer,
  SectionTitle,
  SimilarGamesDescription,
  SimilarGamesSection,
  TagCategory,
  TagCategoryTitle,
  TagChipsContainer,
  TagsGrid,
} from "./styled";

const GameDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const gameId = id ? Number.parseInt(id) : undefined;

  const {
    data: game,
    isLoading: gameLoading,
    error: gameError,
    refetch: refetchGame,
  } = useGame(gameId);

  const { data: recommendationsData, isLoading: similarLoading } =
    useSimilarGames(gameId, 12, true);

  if (gameLoading) {
    return <LoadingSpinner message="Loading game details..." />;
  }

  if (gameError || !game) {
    return (
      <ErrorDisplay
        error={gameError ?? new Error("Game not found")}
        onRetry={() => refetchGame()}
      />
    );
  }

  const similarGames = recommendationsData?.detailedRecommendations ?? [];
  const year = game.releaseDate ? game.releaseDate.split("-")[0] : null;

  return (
    <PageContainer>
      <BackButtonContainer>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)}>
          Back
        </Button>
      </BackButtonContainer>

      <HeroSection>
        {game.backgroundImageUrl && (
          <GameImage
            src={game.backgroundImageUrl}
            alt={`Cover art for ${game.name}`}
          />
        )}

        <GameTitle>{game.name}</GameTitle>

        <GameMetadata>
          {game.rating && (
            <RatingContainer>
              <Rating value={game.rating / 2} readOnly precision={0.1} />
              <Typography variant="body2" color="text.secondary">
                {game.rating.toFixed(1)}/10
              </Typography>
              <Typography variant="caption" color="text.secondary">
                ({game.ratingCount.toLocaleString()} ratings)
              </Typography>
            </RatingContainer>
          )}
          {year && <Chip label={year} size="small" />}
        </GameMetadata>
      </HeroSection>

      {game.description && (
        <SectionContainer>
          <SectionTitle>About</SectionTitle>
          <Typography
            variant="body1"
            color="text.secondary"
            sx={{ whiteSpace: "pre-wrap", lineHeight: 1.7 }}
          >
            {game.description}
          </Typography>
        </SectionContainer>
      )}

      {game.tagsByCategory && Object.keys(game.tagsByCategory).length > 0 && (
        <SectionContainer>
          <SectionTitle>Tags</SectionTitle>
          <TagsGrid>
            {Object.entries(game.tagsByCategory)
              .filter(([category]) => category !== "KEYWORD")
              .map(([category, tags]) => (
                <TagCategory key={category}>
                  <TagCategoryTitle>{category}</TagCategoryTitle>
                  <TagChipsContainer>
                    {tags.map((tag) => (
                      <Chip
                        key={tag.id}
                        label={tag.name}
                        size="small"
                        variant="outlined"
                      />
                    ))}
                  </TagChipsContainer>
                </TagCategory>
              ))}
          </TagsGrid>
        </SectionContainer>
      )}

      {similarGames.length > 0 && (
        <>
          <Divider />
          <SimilarGamesSection>
            <GameTitle>Similar Games</GameTitle>
            <SimilarGamesDescription>
              Based on content analysis and gameplay similarity
            </SimilarGamesDescription>

            {similarLoading ? (
              <LoadingSpinner message="Finding similar games..." />
            ) : (
              <GameGrid games={similarGames} variant="similar" />
            )}
          </SimilarGamesSection>
        </>
      )}
    </PageContainer>
  );
};

export default GameDetailPage;

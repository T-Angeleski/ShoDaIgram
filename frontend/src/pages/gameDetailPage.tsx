import { useNavigate, useParams } from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { Box, Button, Chip, Divider, Rating, Typography } from "@mui/material";

import ErrorDisplay from "../components/common/errorDisplay";
import LoadingSpinner from "../components/common/loadingSpinner";
import PageContainer from "../components/common/pageContainer";
import GameGrid from "../components/game/gameGrid";
import { BackButtonContainer, HeroSection } from "../components/game/styled";
import { useGame } from "../hooks/queries/useGame";
import { useSimilarGames } from "../hooks/queries/useSimilarGames";

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
          <Box
            component="img"
            src={game.backgroundImageUrl}
            alt={`Cover art for ${game.name}`}
            sx={{
              width: "100%",
              height: "500px",
              objectFit: "cover",
              objectPosition: "center 30%",
              borderRadius: 2,
              mb: 3,
            }}
          />
        )}

        <Typography variant="h3" component="h1" gutterBottom fontWeight={600}>
          {game.name}
        </Typography>

        <Box sx={{ display: "flex", alignItems: "center", gap: 2, mb: 2 }}>
          {game.rating && (
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <Rating value={game.rating / 2} readOnly precision={0.1} />
              <Typography variant="body2" color="text.secondary">
                {game.rating.toFixed(1)}/10
              </Typography>
              <Typography variant="caption" color="text.secondary">
                ({game.ratingCount.toLocaleString()} ratings)
              </Typography>
            </Box>
          )}
          {year && <Chip label={year} size="small" />}
        </Box>
      </HeroSection>

      {game.description && (
        <Box sx={{ mb: 4 }}>
          <Typography variant="h5" gutterBottom>
            About
          </Typography>
          <Typography
            variant="body1"
            color="text.secondary"
            sx={{ whiteSpace: "pre-wrap", lineHeight: 1.7 }}
          >
            {game.description}
          </Typography>
        </Box>
      )}

      {game.tagsByCategory && Object.keys(game.tagsByCategory).length > 0 && (
        <Box sx={{ mb: 4 }}>
          <Typography variant="h5" gutterBottom>
            Tags
          </Typography>
          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: {
                xs: "1fr",
                sm: "repeat(2, 1fr)",
                md: "repeat(3, 1fr)",
              },
              gap: 2,
            }}
          >
            {Object.entries(game.tagsByCategory)
              .filter(([category]) => category !== "KEYWORD")
              .map(([category, tags]) => (
                <Box key={category}>
                  <Typography
                    variant="subtitle2"
                    color="text.secondary"
                    sx={{ mb: 1, fontWeight: 600 }}
                  >
                    {category}
                  </Typography>
                  <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                    {tags.map((tag) => (
                      <Chip
                        key={tag.id}
                        label={tag.name}
                        size="small"
                        variant="outlined"
                      />
                    ))}
                  </Box>
                </Box>
              ))}
          </Box>
        </Box>
      )}

      {similarGames.length > 0 && (
        <>
          <Divider sx={{ my: 6 }} />
          <Box>
            <Typography variant="h4" gutterBottom fontWeight={600}>
              Similar Games
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Based on content analysis and gameplay similarity
            </Typography>

            {similarLoading ? (
              <LoadingSpinner message="Finding similar games..." />
            ) : (
              <GameGrid games={similarGames} variant="similar" />
            )}
          </Box>
        </>
      )}
    </PageContainer>
  );
};

export default GameDetailPage;

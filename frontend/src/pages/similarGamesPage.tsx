import { useNavigate, useParams } from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { Box, Button, Typography } from "@mui/material";

import ErrorDisplay from "../components/common/errorDisplay";
import LoadingSpinner from "../components/common/loadingSpinner";
import PageContainer from "../components/common/pageContainer";
import GameGrid from "../components/game/gameGrid";
import { BackButtonContainer, HeroSection } from "../components/game/styled";
import { useGame } from "../hooks/queries/useGame";
import { useSimilarGames } from "../hooks/queries/useSimilarGames";

const SimilarGamesPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const gameId = id ? Number.parseInt(id) : undefined;

  const {
    data: game,
    isLoading: gameLoading,
    error: gameError,
    refetch: refetchGame,
  } = useGame(gameId);
  const {
    data: recommendationsData,
    isLoading: similarLoading,
    error: similarError,
    refetch: refetchSimilar,
  } = useSimilarGames(gameId, 20, true);

  if (gameLoading || similarLoading) {
    return <LoadingSpinner message="Finding similar games..." />;
  }

  if (gameError || !game) {
    return (
      <ErrorDisplay
        error={gameError ?? new Error("Game not found")}
        onRetry={() => refetchGame()}
      />
    );
  }

  if (similarError) {
    return (
      <ErrorDisplay error={similarError} onRetry={() => refetchSimilar()} />
    );
  }

  const similarGames = recommendationsData?.detailedRecommendations ?? [];

  return (
    <PageContainer>
      <BackButtonContainer>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate("/games")}
        >
          Back to Browse
        </Button>
      </BackButtonContainer>

      <HeroSection>
        <Typography
          variant="h4"
          component="h1"
          gutterBottom
          sx={{ fontWeight: 600 }}
        >
          Games Similar to {game.name}
        </Typography>
        <Typography variant="body1" sx={{ opacity: 0.9 }}>
          Based on content analysis and gameplay similarity
        </Typography>
      </HeroSection>

      {similarGames.length === 0 ? (
        <Box sx={{ textAlign: "center", py: 8 }}>
          <Typography variant="h5" color="text.secondary">
            No similar games found
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mt: 2 }}>
            This game is quite unique!
          </Typography>
        </Box>
      ) : (
        <GameGrid
          games={similarGames}
          isLoading={false}
          isEmpty={false}
          variant="similar"
        />
      )}
    </PageContainer>
  );
};

export default SimilarGamesPage;

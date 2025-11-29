import { useNavigate } from "react-router-dom";
import ExploreIcon from "@mui/icons-material/Explore";
import SearchIcon from "@mui/icons-material/Search";
import { Button, CardContent } from "@mui/material";

import PageContainer from "../components/common/pageContainer";
import { useGames } from "../hooks/queries/useGames";

import {
  CTACard,
  CTADescription,
  CTAGrid,
  CTATitle,
  HeroSection,
  HeroStats,
  HeroSubtitle,
  HeroTitle,
} from "./styled";

const HomePage = () => {
  const navigate = useNavigate();

  const { data } = useGames({ page: 0, size: 1 });

  const statsText = data
    ? `${data.totalResults.toLocaleString()} games in our collection`
    : "Thousands of games to discover";

  return (
    <PageContainer>
      <HeroSection>
        <HeroTitle>Sho Da Igram?</HeroTitle>
        <HeroSubtitle>Discover your next favorite game</HeroSubtitle>
        {data && <HeroStats>{statsText}</HeroStats>}
      </HeroSection>

      <CTAGrid>
        <CTACard onClick={() => navigate("/search")}>
          <CardContent sx={{ p: 5, textAlign: "center" }}>
            <SearchIcon sx={{ fontSize: 64, color: "#764ba2", mb: 2 }} />
            <CTATitle>Search by text</CTATitle>
            <CTADescription>
              Describe what you're looking for and find similar games using
              advanced text matching
            </CTADescription>
            <Button
              variant="contained"
              size="large"
              sx={{
                background: "linear-gradient(135deg, #764ba2 0%, #667eea 100%)",
                "&:hover": {
                  background:
                    "linear-gradient(135deg, #6a3f8c 0%, #5568d3 100%)",
                },
              }}
            >
              Start Searching
            </Button>
          </CardContent>
        </CTACard>

        <CTACard onClick={() => navigate("/games")}>
          <CardContent sx={{ p: 5, textAlign: "center" }}>
            <ExploreIcon sx={{ fontSize: 64, color: "#667eea", mb: 2 }} />
            <CTATitle>Browse Games</CTATitle>
            <CTADescription>
              Explore our collection and filter by tags to find games that match
              your preferences
            </CTADescription>
            <Button
              variant="contained"
              size="large"
              sx={{
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                "&:hover": {
                  background:
                    "linear-gradient(135deg, #5568d3 0%, #6a3f8c 100%)",
                },
              }}
            >
              Start Browsing
            </Button>
          </CardContent>
        </CTACard>
      </CTAGrid>
    </PageContainer>
  );
};

export default HomePage;

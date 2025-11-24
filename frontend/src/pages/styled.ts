import { Box, Card, Typography } from "@mui/material";
import styled from "styled-components";

// Home Page Styles
export const HeroSection = styled(Box)`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 48px;
  text-align: center;
  color: white;
  margin-bottom: 48px;

  @media (max-width: 768px) {
    padding: 32px;
  }
`;

export const HeroTitle = styled(Typography).attrs({ variant: "h2" })`
  && {
    font-size: 3rem;
    font-weight: 700;
    margin-bottom: 16px;
    color: white;
  }

  @media (max-width: 768px) {
    && {
      font-size: 2rem;
    }
  }
`;

export const HeroSubtitle = styled(Typography).attrs({ variant: "h5" })`
  && {
    font-size: 1.5rem;
    margin-bottom: 16px;
    opacity: 0.95;
    color: white;
  }

  @media (max-width: 768px) {
    && {
      font-size: 1.1rem;
    }
  }
`;

export const HeroStats = styled(Typography).attrs({ variant: "body1" })`
  && {
    font-size: 1rem;
    opacity: 0.9;
    color: white;
  }
`;

export const CTAGrid = styled(Box)`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

export const CTACard = styled(Card)`
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  }
`;

export const CTACardContent = styled(Box)`
  padding: 32px;
  text-align: center;
`;

export const CTATitle = styled(Typography).attrs({ variant: "h4" })`
  && {
    font-size: 1.75rem;
    font-weight: 600;
    margin-bottom: 16px;
  }
`;

export const CTADescription = styled(Typography)`
  && {
    font-size: 1rem;
    color: rgba(0, 0, 0, 0.6);
    margin-bottom: 40px;
    line-height: 1.6;
  }
`;

export const GradientButton = styled(Box)<{
  variant?: "primary" | "secondary";
}>`
  display: inline-block;
  padding: 12px 32px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.2s ease;
  background: ${({ variant }) =>
    variant === "secondary"
      ? "linear-gradient(135deg, #764ba2 0%, #667eea 100%)"
      : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"};
  color: white;
  text-transform: uppercase;
  letter-spacing: 0.5px;

  &:hover {
    opacity: 0.9;
  }
`;

// Game Browser Page Styles
export const PageHeader = styled(Box)`
  margin-bottom: 32px;
`;

export const PageTitle = styled(Typography)`
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 8px;
`;

export const PageDescription = styled(Typography)`
  font-size: 1rem;
  color: rgba(0, 0, 0, 0.6);
`;

// Game Detail Page Styles
export const BackButton = styled(Box)`
  margin-bottom: 24px;
`;

export const GameHeroSection = styled(Box)`
  margin-bottom: 48px;
`;

export const GameImage = styled.img`
  width: 100%;
  height: 500px;
  object-fit: cover;
  object-position: center 30%;
  border-radius: 16px;
  margin-bottom: 24px;
`;

export const GameTitle = styled(Typography)`
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 16px;

  @media (max-width: 768px) {
    font-size: 2rem;
  }
`;

export const GameMetadata = styled(Box)`
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
`;

export const RatingContainer = styled(Box)`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const SectionContainer = styled(Box)`
  margin-bottom: 32px;
`;

export const SectionTitle = styled(Typography)`
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 16px;
`;

export const TagsGrid = styled(Box)`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;

  @media (max-width: 960px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
`;

export const TagCategory = styled(Box)`
  display: flex;
  flex-direction: column;
`;

export const TagCategoryTitle = styled(Typography)`
  font-size: 0.875rem;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 8px;
`;

export const TagChipsContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
`;

export const Divider = styled(Box)`
  height: 1px;
  background-color: rgba(0, 0, 0, 0.12);
  margin: 48px 0;
`;

export const SimilarGamesSection = styled(Box)`
  margin-top: 48px;
`;

export const SimilarGamesDescription = styled(Typography)`
  font-size: 1rem;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 24px;
`;

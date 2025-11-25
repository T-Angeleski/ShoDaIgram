import { Alert, Box, Card, Typography } from "@mui/material";
import styled from "styled-components";

import { BRAND_COLORS, UI_COLORS } from "../utils/colors";

// Home Page Styles
export const HeroSection = styled(Box)`
  background: ${BRAND_COLORS.GRADIENT};
  border-radius: ${({ theme }) => theme.spacing(3)};
  padding: ${({ theme }) => theme.spacing(6)};
  text-align: center;
  color: ${UI_COLORS.WHITE};
  margin-bottom: ${({ theme }) => theme.spacing(6)};

  @media (max-width: 768px) {
    padding: ${({ theme }) => theme.spacing(4)};
  }
`;

export const HeroTitle = styled(Typography).attrs({ variant: "h2" })`
  && {
    font-size: 3rem;
    font-weight: 700;
    margin-bottom: ${({ theme }) => theme.spacing(2)};
    color: ${UI_COLORS.WHITE};
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
    margin-bottom: ${({ theme }) => theme.spacing(2)};
    opacity: 0.95;
    color: ${UI_COLORS.WHITE};
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
    color: ${UI_COLORS.WHITE};
  }
`;

export const CTAGrid = styled(Box)`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: ${({ theme }) => theme.spacing(4)};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

export const CTACard = styled(Card)`
  cursor: pointer;
  transition:
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 16px 32px rgba(102, 126, 234, 0.2);
  }
`;

export const CTACardContent = styled(Box)`
  padding: ${({ theme }) => theme.spacing(4)};
  text-align: center;
`;

export const CTATitle = styled(Typography).attrs({ variant: "h4" })`
  && {
    font-size: 1.75rem;
    font-weight: 600;
    margin-bottom: ${({ theme }) => theme.spacing(2)};
  }
`;

export const CTADescription = styled(Typography)`
  && {
    font-size: 1rem;
    color: ${UI_COLORS.TEXT_SECONDARY};
    margin-bottom: ${({ theme }) => theme.spacing(5)};
    line-height: 1.6;
  }
`;

// prettier-ignore
export const GradientButton = styled(Box)<{
  variant?: "primary" | "secondary";
}>`
  display: inline-block;
  padding: ${({ theme }) => theme.spacing(1.5)} ${({ theme }) => theme.spacing(4)};
  font-size: 1rem;
  font-weight: 600;
  border-radius: ${({ theme }) => theme.spacing(1)};
  cursor: pointer;
  transition: opacity 0.2s ease;
  background: ${({ variant }) =>
    variant === "secondary"
      ? BRAND_COLORS.GRADIENT_REVERSE
      : BRAND_COLORS.GRADIENT};
  color: ${UI_COLORS.WHITE};
  text-transform: uppercase;
  letter-spacing: 0.5px;

  &:hover {
    opacity: 0.9;
  }
`;

// Game Browser Page Styles
export const PageHeader = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(4)};
`;

export const PageTitle = styled(Typography)`
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: ${({ theme }) => theme.spacing(1)};
`;

export const PageDescription = styled(Typography)`
  font-size: 1rem;
  color: ${UI_COLORS.TEXT_SECONDARY};
`;

export const FilterFormControl = styled(Box)`
  min-width: 200px;
  margin-bottom: ${({ theme }) => theme.spacing(2)};
`;

// Game Detail Page Styles
export const BackButton = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(3)};
`;

export const GameHeroSection = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(6)};
`;

export const GameImage = styled.img`
  width: 100%;
  height: 500px;
  object-fit: cover;
  object-position: center 30%;
  border-radius: ${({ theme }) => theme.spacing(2)};
  margin-bottom: ${({ theme }) => theme.spacing(3)};
`;

export const GameTitle = styled(Typography)`
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: ${({ theme }) => theme.spacing(2)};

  @media (max-width: 768px) {
    font-size: 2rem;
  }
`;

export const GameMetadata = styled(Box)`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing(2)};
  margin-bottom: ${({ theme }) => theme.spacing(2)};
  flex-wrap: wrap;
`;

export const RatingContainer = styled(Box)`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing(1)};
`;

export const SectionContainer = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(4)};
`;

export const SectionTitle = styled(Typography)`
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: ${({ theme }) => theme.spacing(2)};
`;

export const GameDescriptionText = styled(Typography)`
  white-space: pre-wrap;
  line-height: 1.7;
`;

export const TagsGrid = styled(Box)`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: ${({ theme }) => theme.spacing(2)};

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
  color: ${UI_COLORS.TEXT_SECONDARY};
  margin-bottom: ${({ theme }) => theme.spacing(1)};
`;

export const TagChipsContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing(1)};
`;

export const Divider = styled(Box)`
  height: 1px;
  background-color: ${UI_COLORS.DIVIDER};
  margin: ${({ theme }) => theme.spacing(6)} 0;
`;

export const InlineErrorAlert = styled(Alert)`
  margin: ${({ theme }) => theme.spacing(2)} 0;
  max-width: 800px;
`;

export const SimilarGamesSection = styled(Box)`
  margin-top: ${({ theme }) => theme.spacing(6)};
`;

export const SimilarGamesDescription = styled(Typography)`
  font-size: 1rem;
  color: ${UI_COLORS.TEXT_SECONDARY};
  margin-bottom: ${({ theme }) => theme.spacing(3)};
`;

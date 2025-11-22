import { Box, Card } from "@mui/material";
import styled from "styled-components";

export const StyledGameCard = styled(Card)`
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  will-change: transform;

  &:hover {
    transform: translate3d(0, -4px, 0);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
`;

export const GameCardImage = styled.img`
  width: 100%;
  height: auto;
  aspect-ratio: 16 / 9;
  object-fit: cover;
  background-color: #e0e0e0;
`;

export const GameCardContent = styled(Box)`
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

export const GameTitle = styled(Box)`
  font-size: 1.125rem;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 2.8em;
`;

export const GameMetadata = styled(Box)`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
`;

export const GameGridContainer = styled(Box)`
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 24px;

  @media (min-width: 600px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (min-width: 960px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (min-width: 1280px) {
    grid-template-columns: repeat(4, 1fr);
  }
`;

export const SimilarGameCardWrapper = styled(Card)`
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  position: relative;
  overflow: visible;
  will-change: transform;

  &:hover {
    transform: translate3d(0, -4px, 0);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
`;

export const SimilarityBadge = styled(Box)<{ score: number }>`
  position: absolute;
  top: 12px;
  right: 12px;
  background-color: ${({ score }) =>
    score >= 0.8 ? "#22c55e" : score >= 0.6 ? "#f59e0b" : "#64748b"};
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-weight: 600;
  font-size: 0.875rem;
  z-index: 1;
`;

export const MatchReasonsContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
`;

export const MatchBadge = styled(Box)<{ reasonType: string }>`
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  background-color: ${({ reasonType }) => {
    switch (reasonType) {
      case "GENRE_MATCH":
        return "#3b82f6";
      case "THEME_MATCH":
        return "#8b5cf6";
      case "FRANCHISE_MATCH":
        return "#ec4899";
      case "DESCRIPTION_SIMILARITY":
        return "#10b981";
      default:
        return "#64748b";
    }
  }};
  color: white;
`;

export const FilterSection = styled(Box)`
  margin-bottom: 32px;
  padding: 24px;
  background-color: #f9fafb;
  border-radius: 8px;
`;

export const FilterTitle = styled(Box)`
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 16px;
`;

export const HeroSection = styled(Box)`
  margin-bottom: 48px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
`;

export const HeroGameCard = styled(Card)`
  max-width: 600px;
  margin: 0 auto;
`;

export const BackButtonContainer = styled(Box)`
  margin-bottom: 24px;
`;

export const SectionTitle = styled(Box)`
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: ${({ theme }) => theme.palette.text.primary};
`;

export const TagFilterContainer = styled(Box)`
  margin-bottom: 24px;
  width: 100%;
`;

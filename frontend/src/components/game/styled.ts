import { Box, Card } from "@mui/material";
import styled from "styled-components";

import {
  BRAND_COLORS,
  getMatchReasonColor,
  getSimilarityColor,
} from "../../utils/colors";

export const StyledGameCard = styled(Card)`
  cursor: pointer;
  transition:
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  will-change: transform;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translate3d(0, -8px, 0);
    box-shadow: 0 12px 24px rgba(102, 126, 234, 0.25);
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
  padding: ${({ theme }) => theme.spacing(2)};
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing(1)};
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
  gap: ${({ theme }) => theme.spacing(3)};

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
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: visible;
  will-change: transform;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translate3d(0, -8px, 0);
    box-shadow: 0 12px 24px rgba(102, 126, 234, 0.25);
  }
`;

// prettier-ignore
export const SimilarityBadge = styled(Box)<{ score: number }>`
  position: absolute;
  top: ${({ theme }) => theme.spacing(1.5)};
  right: ${({ theme }) => theme.spacing(1.5)};
  background-color: ${({ score }) => getSimilarityColor(score)};
  color: white;
  padding: ${({ theme }) => theme.spacing(0.5)} ${({ theme }) => theme.spacing(1.5)};
  border-radius: ${({ theme }) => theme.spacing(2)};
  font-weight: 600;
  font-size: 0.875rem;
  z-index: 1;
`;

export const MatchReasonsContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing(1)};
  margin-top: ${({ theme }) => theme.spacing(1)};
`;

// prettier-ignore
export const MatchBadge = styled(Box)<{ $reasonType: string }>`
  display: inline-flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing(0.5)};
  padding: ${({ theme }) => theme.spacing(0.5)} ${({ theme }) => theme.spacing(1.25)};
  border-radius: ${({ theme }) => theme.spacing(1.5)};
  font-size: 0.75rem;
  font-weight: 500;
  background-color: ${({ $reasonType }) => getMatchReasonColor($reasonType)};
  color: white;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;

  &:hover {
    transform: scale(1.05);
  }
`;

export const FilterSection = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(4)};
  padding: ${({ theme }) => theme.spacing(3)};
  background-color: #f9fafb;
  border-radius: ${({ theme }) => theme.spacing(1)};
`;

export const FilterTitle = styled(Box)`
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: ${({ theme }) => theme.spacing(2)};
`;

export const HeroSection = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(6)};
  padding: ${({ theme }) => theme.spacing(4)};
  background: ${BRAND_COLORS.GRADIENT};
  border-radius: ${({ theme }) => theme.spacing(2)};
  color: white;
`;

export const HeroGameCard = styled(Card)`
  max-width: 600px;
  margin: 0 auto;
`;

export const BackButtonContainer = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(3)};
`;

export const SectionTitle = styled(Box)`
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: ${({ theme }) => theme.spacing(3)};
  color: ${({ theme }) => theme.palette.text.primary};
`;

export const TagFilterContainer = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(3)};
  width: 100%;
`;

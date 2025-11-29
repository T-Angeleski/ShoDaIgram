import { Box, Typography } from "@mui/material";
import styled from "styled-components";

export const SearchContainer = styled(Box)`
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
`;

export const SearchHeroSection = styled(Box)`
  text-align: center;
  margin-bottom: 48px;
`;

export const ExampleQueriesContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  margin-top: 16px;
`;

// Carousel components
export const CarouselSection = styled(Box)`
  width: 100%;
  overflow: hidden;
  margin-top: 64px;
  margin-bottom: 32px;
`;

export const CarouselContainer = styled.div`
  width: 100%;
  overflow: hidden;
  margin-bottom: 24px;
  position: relative;

  &::before,
  &::after {
    content: "";
    position: absolute;
    top: 0;
    bottom: 0;
    width: 100px;
    z-index: 2;
    pointer-events: none;
  }

  &::before {
    left: 0;
    background: linear-gradient(
      to right,
      rgba(18, 18, 18, 1) 0%,
      rgba(18, 18, 18, 0) 100%
    );
  }

  &::after {
    right: 0;
    background: linear-gradient(
      to left,
      rgba(18, 18, 18, 1) 0%,
      rgba(18, 18, 18, 0) 100%
    );
  }
`;

interface CarouselTrackProps {
  direction: "left-to-right" | "right-to-left";
}

export const CarouselTrack = styled.div<CarouselTrackProps>`
  display: flex;
  gap: 16px;
  animation: ${(props) =>
      props.direction === "left-to-right" ? "scrollLeft" : "scrollRight"}
    60s linear infinite;
  will-change: transform;

  &:hover {
    animation-play-state: paused;
  }

  @keyframes scrollLeft {
    0% {
      transform: translateX(0);
    }
    100% {
      transform: translateX(-33.333%);
    }
  }

  @keyframes scrollRight {
    0% {
      transform: translateX(-33.333%);
    }
    100% {
      transform: translateX(0);
    }
  }
`;

interface GameImageCardProps {
  $hasImage: boolean;
}

export const GameImageCard = styled.div<GameImageCardProps>`
  position: relative;
  flex-shrink: 0;
  width: 280px;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  background: ${(props) =>
    props.$hasImage
      ? "transparent"
      : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"};
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    filter: grayscale(0.3);
    transition:
      filter 0.3s ease,
      transform 0.3s ease;
  }

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);

    img {
      filter: grayscale(0);
      transform: scale(1.1);
    }
  }
`;

export const GameImageOverlay = styled.div`
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.9) 0%,
    rgba(0, 0, 0, 0.6) 60%,
    rgba(0, 0, 0, 0) 100%
  );
  display: flex;
  align-items: flex-end;
`;

export const GameTitle = styled(Typography)`
  color: white;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.8);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
`;

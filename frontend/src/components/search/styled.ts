import { Box, Typography } from "@mui/material";
import styled from "styled-components";

export const SearchContainer = styled(Box)`
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
`;

export const SearchHeroSection = styled(Box)`
  text-align: center;
  margin-bottom: ${({ theme }) => theme.spacing(6)};
`;

export const SearchHeaderContainer = styled(Box)`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: ${({ theme }) => theme.spacing(3)};
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
`;

export const ClearLink = styled(Typography)`
  cursor: pointer;
  text-decoration: underline;
  &:hover {
    opacity: 0.8;
  }
`;

export const EmptyStateContainer = styled(Box)`
  text-align: center;
  padding: ${({ theme }) => theme.spacing(6)} 0;
`;

export const EmptyStateDescription = styled(Typography)`
  margin-bottom: ${({ theme }) => theme.spacing(3)};
`;

export const EmptyStateActions = styled(Box)`
  margin-top: ${({ theme }) => theme.spacing(3)};
`;

export const SearchResultsHeader = styled(Box)`
  margin-bottom: ${({ theme }) => theme.spacing(3)};
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing(2)};
`;

export const HeroDescription = styled(Typography)`
  margin-bottom: ${({ theme }) => theme.spacing(3)};
`;

export const ExampleQueriesContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing(1.5)};
  justify-content: center;
  margin-top: ${({ theme }) => theme.spacing(2)};
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
`;

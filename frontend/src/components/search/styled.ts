import { Box } from "@mui/material";
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

export const ExampleQueriesContainer = styled(Box)`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing(1.5)};
  justify-content: center;
  margin-top: ${({ theme }) => theme.spacing(2)};
`;

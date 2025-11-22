import { Box } from "@mui/material";
import styled from "styled-components";

export const LoadingContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: ${({ theme }) => theme.spacing(2)};
  min-height: 200px;
`;

export const ErrorContainer = styled(Box)`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  padding: ${({ theme }) => theme.spacing(3)};
`;

export const EmptyStateContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: ${({ theme }) => theme.spacing(2)};
  min-height: 300px;
  padding: ${({ theme }) => theme.spacing(4)};
  text-align: center;
`;

export const PageContainerWrapper = styled(Box)`
  max-width: 1400px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing(3)};

  @media (max-width: 768px) {
    padding: ${({ theme }) => theme.spacing(2)};
  }
`;

export const PaginationContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${({ theme }) => theme.spacing(2)};
  margin-top: ${({ theme }) => theme.spacing(3)};
`;

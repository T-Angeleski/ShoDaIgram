import { AppBar, Box } from "@mui/material";
import styled from "styled-components";

import { BRAND_COLORS } from "../../utils/colors";

export const StyledAppBar = styled(AppBar)`
  background: ${BRAND_COLORS.GRADIENT} !important;
  background-color: transparent !important;
`;

export const HeaderContent = styled(Box)`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: ${({ theme }) => theme.spacing(2)} ${({ theme }) => theme.spacing(3)};
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
`;

export const Logo = styled(Box)`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing(1)};
  cursor: pointer;
  text-decoration: none;
  color: inherit;

  &:hover {
    opacity: 0.9;
  }
`;

export const NavLinks = styled(Box)`
  display: flex;
  gap: ${({ theme }) => theme.spacing(3)};
  align-items: center;

  @media (max-width: 768px) {
    gap: ${({ theme }) => theme.spacing(2)};
  }
`;

export const LayoutWrapper = styled(Box)`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
`;

export const MainContent = styled(Box)`
  flex: 1;
  margin-top: ${({ theme }) => theme.spacing(8)}; /* Height of AppBar */
`;

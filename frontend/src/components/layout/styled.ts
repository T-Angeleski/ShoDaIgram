import { AppBar, Box } from "@mui/material";
import styled from "styled-components";

export const StyledAppBar = styled(AppBar)`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  background-color: transparent !important;
`;

export const HeaderContent = styled(Box)`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
`;

export const Logo = styled(Box)`
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  text-decoration: none;
  color: inherit;

  &:hover {
    opacity: 0.9;
  }
`;

export const NavLinks = styled(Box)`
  display: flex;
  gap: 24px;
  align-items: center;

  @media (max-width: 768px) {
    gap: 16px;
  }
`;

export const LayoutWrapper = styled(Box)`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
`;

export const MainContent = styled(Box)`
  flex: 1;
  margin-top: 64px; /* Height of AppBar */
`;

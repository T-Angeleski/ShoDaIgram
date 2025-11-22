import { ReactNode } from "react";

import AppHeader from "./appHeader";
import { LayoutWrapper, MainContent } from "./styled";

interface AppLayoutProps {
  children: ReactNode;
}

const AppLayout = ({ children }: AppLayoutProps) => {
  return (
    <LayoutWrapper>
      <AppHeader />
      <MainContent>{children}</MainContent>
    </LayoutWrapper>
  );
};

export default AppLayout;

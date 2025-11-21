import { ReactNode } from "react";

import { PageContainerWrapper } from "./styled";

interface PageContainerProps {
  children: ReactNode;
}

const PageContainer = ({ children }: PageContainerProps) => {
  return <PageContainerWrapper>{children}</PageContainerWrapper>;
};

export default PageContainer;

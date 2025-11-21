import { BrowserRouter, Route, Routes } from "react-router-dom";
import { CssBaseline } from "@mui/material";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "styled-components";

import AppLayout from "./components/layout/appLayout";
import { theme } from "./theme/theme";
import { FIVE_MINUTES } from "./utils/appConstants";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: FIVE_MINUTES,
      refetchOnWindowFocus: false,
      retry: 3,
    },
  },
});

// Placeholder pages
const HomePage = () => (
  <div style={{ padding: "24px" }}>Home Page - Coming soon...</div>
);
const GameBrowserPage = () => (
  <div style={{ padding: "24px" }}>Game Browser - Coming soon...</div>
);
const SearchPage = () => (
  <div style={{ padding: "24px" }}>Search Page - Coming soon...</div>
);

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <AppLayout>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/games" element={<GameBrowserPage />} />
              <Route path="/search" element={<SearchPage />} />
              <Route
                path="*"
                element={
                  <div style={{ padding: "24px" }}>404 - Page Not Found</div>
                }
              />
            </Routes>
          </AppLayout>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;

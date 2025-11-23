import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { CssBaseline } from "@mui/material";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "styled-components";

import AppLayout from "./components/layout/appLayout";
import GameBrowserPage from "./pages/gameBrowserPage";
import GameDetailPage from "./pages/gameDetailPage";
import SearchPage from "./pages/searchPage";
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
              <Route path="/games/:id" element={<GameDetailPage />} />
              <Route path="/search" element={<SearchPage />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </AppLayout>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;

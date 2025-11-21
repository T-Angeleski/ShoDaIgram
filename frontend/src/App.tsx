import { useEffect, useState } from "react";
import { Box, CircularProgress, CssBaseline, Typography } from "@mui/material";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "styled-components";

import { gameService } from "./services/gameService";
import { theme } from "./theme/theme";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      refetchOnWindowFocus: false,
      retry: 3,
    },
  },
});

function AppContent() {
  const [status, setStatus] = useState<"loading" | "success" | "error">(
    "loading",
  );
  const [message, setMessage] = useState("Testing backend connection...");

  useEffect(() => {
    gameService
      .testConnection()
      .then((data) => {
        setStatus("success");
        setMessage(`✅ Backend connected! Found ${data.totalResults} games.`);
      })
      .catch((error) => {
        setStatus("error");
        setMessage(
          `❌ Backend connection failed: ${error?.message || "Unknown error"}`,
        );
      });
  }, []);

  const getMessageColor = (): string => {
    if (status === "success") return "success.main";
    if (status === "error") return "error.main";
    return "text.primary";
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: 2,
      }}
    >
      <Typography variant="h1">ShoDaIgram</Typography>
      <Typography variant="h3">Game Recommendations</Typography>

      {status === "loading" && <CircularProgress />}

      <Typography variant="body1" color={getMessageColor()}>
        {message}
      </Typography>
    </Box>
  );
}

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AppContent />
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;

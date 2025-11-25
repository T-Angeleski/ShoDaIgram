import { useNavigate } from "react-router-dom";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import HomeIcon from "@mui/icons-material/Home";
import RefreshIcon from "@mui/icons-material/Refresh";
import SearchIcon from "@mui/icons-material/Search";
import { Alert, AlertTitle, Box, Button, Typography } from "@mui/material";

import { ErrorResponse } from "../../types/api.types";

import { ErrorContainer } from "./styled";

interface ErrorDisplayProps {
  error: Error | ErrorResponse;
  onRetry?: () => void;
}

interface ErrorConfig {
  title: string;
  message: string;
  actionLabel?: string;
  actionIcon?: React.ReactNode;
  actionPath?: string;
}

const ErrorDisplay = ({ error, onRetry }: ErrorDisplayProps) => {
  const navigate = useNavigate();

  const isApiError = (err: Error | ErrorResponse): err is ErrorResponse => {
    return "status" in err && "path" in err;
  };

  const getErrorConfig = (): ErrorConfig => {
    if (!isApiError(error)) {
      return {
        title: "Connection Error",
        message:
          "Unable to connect to the server. Please check your internet connection and try again.",
        actionLabel: "Retry",
        actionIcon: <RefreshIcon />,
      };
    }

    switch (error.status) {
      case 404:
        return {
          title: "Not Found",
          message:
            "Oops! We couldn't find what you're looking for. It may have been removed or doesn't exist.",
          actionLabel: "Browse All Games",
          actionIcon: <SearchIcon />,
          actionPath: "/games",
        };
      case 500:
      case 502:
      case 503:
        return {
          title: "Server Error",
          message:
            "Something went wrong on our end. Please try again in a few moments.",
          actionLabel: "Try Again",
          actionIcon: <RefreshIcon />,
        };
      case 400:
        return {
          title: "Invalid Request",
          message:
            error.message ||
            "The request could not be processed. Please check your input and try again.",
          actionLabel: "Go Home",
          actionIcon: <HomeIcon />,
          actionPath: "/",
        };
      default:
        return {
          title: "Error",
          message:
            error.message || "An unexpected error occurred. Please try again.",
          actionLabel: "Retry",
          actionIcon: <RefreshIcon />,
        };
    }
  };

  const config = getErrorConfig();

  const handleAction = () => {
    if (config.actionPath) {
      navigate(config.actionPath);
    } else if (onRetry) {
      onRetry();
    }
  };

  return (
    <ErrorContainer>
      <Alert
        severity="error"
        icon={<ErrorOutlineIcon fontSize="large" />}
        sx={{ maxWidth: "600px", width: "100%" }}
      >
        <AlertTitle>{config.title}</AlertTitle>
        <Typography variant="body2" sx={{ mb: 2 }}>
          {config.message}
        </Typography>
        <Box sx={{ display: "flex", gap: 1 }}>
          {(config.actionPath || onRetry) && (
            <Button
              variant="contained"
              size="small"
              startIcon={config.actionIcon}
              onClick={handleAction}
            >
              {config.actionLabel}
            </Button>
          )}
          {config.actionPath && (
            <Button
              variant="outlined"
              size="small"
              startIcon={<HomeIcon />}
              onClick={() => navigate("/")}
            >
              Go Home
            </Button>
          )}
        </Box>
      </Alert>
    </ErrorContainer>
  );
};

export default ErrorDisplay;

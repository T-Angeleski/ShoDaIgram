import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import { Alert, AlertTitle, Button, Typography } from "@mui/material";

import { ErrorResponse } from "../../types/api.types";

import { ErrorContainer } from "./styled";

interface ErrorDisplayProps {
  error: Error | ErrorResponse;
  onRetry?: () => void;
}

const ErrorDisplay = ({ error, onRetry }: ErrorDisplayProps) => {
  const isApiError = (err: Error | ErrorResponse): err is ErrorResponse => {
    return "status" in err && "path" in err;
  };

  const errorMessage = isApiError(error)
    ? error.message
    : (error.message ?? "An unexpected error occurred");

  const errorDetails = isApiError(error) ? `Status: ${error.status}` : null;

  return (
    <ErrorContainer>
      <Alert
        severity="error"
        icon={<ErrorOutlineIcon fontSize="large" />}
        action={
          onRetry && (
            <Button color="inherit" size="small" onClick={onRetry}>
              Retry
            </Button>
          )
        }
        sx={{ maxWidth: "600px", width: "100%" }}
      >
        <AlertTitle>Error</AlertTitle>
        {errorMessage}
        {errorDetails && (
          <Typography variant="caption" display="block" sx={{ mt: 1 }}>
            {errorDetails}
          </Typography>
        )}
      </Alert>
    </ErrorContainer>
  );
};

export default ErrorDisplay;

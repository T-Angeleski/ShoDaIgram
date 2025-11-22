import { CircularProgress, Typography } from "@mui/material";

import { LoadingContainer } from "./styled";

interface LoadingSpinnerProps {
  message?: string;
}

const LoadingSpinner = ({ message }: LoadingSpinnerProps) => {
  return (
    <LoadingContainer>
      <CircularProgress />
      {message && (
        <Typography variant="body1" color="text.secondary">
          {message}
        </Typography>
      )}
    </LoadingContainer>
  );
};

export default LoadingSpinner;

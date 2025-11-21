import InboxIcon from "@mui/icons-material/Inbox";
import { Button, Typography } from "@mui/material";

import { EmptyStateContainer } from "./styled";

interface EmptyStateProps {
  message: string;
  actionLabel?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
}

const EmptyState = ({
  message,
  actionLabel,
  onAction,
  icon,
}: EmptyStateProps) => {
  return (
    <EmptyStateContainer>
      {icon ?? <InboxIcon sx={{ fontSize: 64, color: "text.secondary" }} />}
      <Typography variant="h3" color="text.secondary">
        {message}
      </Typography>
      {actionLabel && onAction && (
        <Button variant="contained" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </EmptyStateContainer>
  );
};

export default EmptyState;

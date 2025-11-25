import { useCallback, useMemo, useState } from "react";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import ErrorIcon from "@mui/icons-material/Error";
import InfoIcon from "@mui/icons-material/Info";
import WarningIcon from "@mui/icons-material/Warning";
import { Alert, Snackbar } from "@mui/material";

type ToastSeverity = "success" | "error" | "warning" | "info";

interface ToastState {
  open: boolean;
  message: string;
  severity: ToastSeverity;
}

interface ToastOptions {
  severity?: ToastSeverity;
  duration?: number;
}

/**
 * Hook for displaying toast notifications with MUI Snackbar.
 * Provides convenience methods for success, error, warning, and info toasts.
 * All functions are memoized with useCallback to prevent unnecessary re-renders.
 *
 * @returns Object with toast functions and ToastComponent to render
 * @example
 * const { showError, showSuccess, ToastComponent } = useToast();
 * showError("Failed to load data");
 * return <>{ToastComponent()}</>
 */
export const useToast = () => {
  const [toast, setToast] = useState<ToastState>({
    open: false,
    message: "",
    severity: "info",
  });

  const showToast = useCallback(
    (message: string, options: ToastOptions = {}) => {
      setToast({
        open: true,
        message,
        severity: options.severity ?? "info",
      });
    },
    [],
  );

  const hideToast = useCallback(() => {
    setToast((prev) => ({ ...prev, open: false }));
  }, []);

  const showSuccess = useCallback(
    (message: string) => {
      showToast(message, { severity: "success" });
    },
    [showToast],
  );

  const showError = useCallback(
    (message: string) => {
      showToast(message, { severity: "error" });
    },
    [showToast],
  );

  const showWarning = useCallback(
    (message: string) => {
      showToast(message, { severity: "warning" });
    },
    [showToast],
  );

  const showInfo = useCallback(
    (message: string) => {
      showToast(message, { severity: "info" });
    },
    [showToast],
  );

  const getIcon = (severity: ToastSeverity) => {
    switch (severity) {
      case "success":
        return <CheckCircleIcon fontSize="inherit" />;
      case "error":
        return <ErrorIcon fontSize="inherit" />;
      case "warning":
        return <WarningIcon fontSize="inherit" />;
      case "info":
      default:
        return <InfoIcon fontSize="inherit" />;
    }
  };

  const ToastComponent = useMemo(
    () => (
      <Snackbar
        open={toast.open}
        autoHideDuration={5000}
        onClose={hideToast}
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      >
        <Alert
          onClose={hideToast}
          severity={toast.severity}
          variant="filled"
          icon={getIcon(toast.severity)}
          sx={{
            width: "100%",
            maxWidth: "400px",
            boxShadow: 3,
          }}
        >
          {toast.message}
        </Alert>
      </Snackbar>
    ),
    [toast, hideToast],
  );

  return {
    showToast,
    hideToast,
    ToastComponent,
    showSuccess,
    showError,
    showWarning,
    showInfo,
  };
};

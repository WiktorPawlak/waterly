import { Snackbar } from "@mui/material";
import MuiAlert, { AlertColor, AlertProps } from "@mui/material/Alert";
import React from "react";

export const Alert = React.forwardRef<HTMLDivElement, AlertProps>(
  function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
  }
);

export interface ToastProps {
  isToastOpen: boolean;
  setIsToastOpen: (isOpen: boolean) => void;
  severity: AlertColor;
  message: string;
}

export const Toast = ({
  isToastOpen,
  setIsToastOpen,
  severity,
  message,
}: ToastProps) => {
  const handleClose = (
    event?: React.SyntheticEvent | Event,
    reason?: string
  ) => {
    if (reason === "clickaway") {
      return;
    }

    setIsToastOpen(false);
  };

  return (
    <Snackbar open={isToastOpen} autoHideDuration={10000} onClose={handleClose}>
      <Alert onClose={handleClose} severity={severity} sx={{ width: "100%" }}>
        {message}
      </Alert>
    </Snackbar>
  );
};

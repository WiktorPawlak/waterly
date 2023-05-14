import { AlertColor } from "@mui/material";
import { useState } from "react";

export const useToast = () => {
  const [isToastOpen, setIsToastOpen] = useState(false);
  const [severity, setSeverity] = useState<AlertColor>("success");
  const [message, setMessage] = useState("");

  const showSuccessToast = (message: string) => {
    setSeverity("success");
    setMessage(message);
    setIsToastOpen(true);
  };

  const showErrorToast = (message: string) => {
    setSeverity("error");
    setMessage(message);
    setIsToastOpen(true);
  };

  return {
    showSuccessToast,
    showErrorToast,
    isToastOpen,
    setIsToastOpen,
    severity,
    message,
  };
};

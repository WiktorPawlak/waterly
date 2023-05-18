import React from "react";
import ReactDOM from "react-dom/client";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "./common/theme";
import { SnackbarProvider } from "notistack";
import App from "./App";
import "./index.css";
import "./i18n";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <ThemeProvider theme={theme}>
    <SnackbarProvider anchorOrigin={{ vertical: "top", horizontal: "right" }}>
      <App />
    </SnackbarProvider>
  </ThemeProvider>
);

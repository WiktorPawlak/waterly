import ReactDOM from "react-dom/client";
import { ThemeProvider } from "@mui/material/styles";
import { theme, darkTheme } from "./common";
import { SnackbarProvider } from "notistack";
import App from "./App";
import "./index.css";
import "./i18n";
import { SnackbarCloseButton } from "./layouts/components/SnackbarCloseButton";

const Root = () => {
  const themeMode = localStorage.getItem("themeMode");

  return (
    <ThemeProvider theme={themeMode === "light" ? theme : darkTheme}>
      <SnackbarProvider
        action={(snackbarKey) => (
          <SnackbarCloseButton snackbarKey={snackbarKey} />
        )}
        anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
        autoHideDuration={null}
      >
        <App />
      </SnackbarProvider>
    </ThemeProvider>
  );
};

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <Root />
);

import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#42A7C2",
      contrastText: "#ffffff",
    },
    secondary: {
      main: "#EE6F2D",
    },
    action: { active: "#E7D428", hover: "#E1935A" },
    text: {
      primary: "#121B2A",
      secondary: "#7D7D7D",
    },
    background: {
      paper: "#ffffff",
      default: "#f5f7f9",
    },
  },
  spacing: 8,
  shape: {
    borderRadius: 35,
  },
  typography: {
    fontFamily: "Inter, sans-serif",
    allVariants: {
      color: "#1A1C20",
    },
  },
});

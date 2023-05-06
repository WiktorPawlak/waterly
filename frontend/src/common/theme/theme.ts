import { createTheme } from "@mui/material/styles";
import { TypographyStyleOptions } from "@mui/material/styles/createTypography";
import { createBreakpoints } from "@mui/system";

const breakpoints = createBreakpoints({});

const customHeaderStyle = (
  fontSize: number,
  fontSizeMd?: number
): TypographyStyleOptions => ({
  fontSize,
  lineHeight: 1.25,
  fontWeight: 600,
  [breakpoints.up("md")]: {
    fontSize: fontSizeMd,
  },
});

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

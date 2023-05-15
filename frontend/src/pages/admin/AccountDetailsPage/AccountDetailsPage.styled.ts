import { TextField, styled } from "@mui/material";

export const StyledTextField = styled(TextField)(({ theme }) => ({
  marginBottom: "16px",
  "& label": {
    color: theme.palette.primary.main,
  },

  "& .MuiInput-underline:before": {
    borderBottom: "2px solid" + theme.palette.primary.main,
  },

  "& .MuiInput-underline:hover:before": {
    borderBottom: "2px solid" + theme.palette.primary.main,
  },

  [theme.breakpoints.up("md")]: {
    width: "45%",
  },
  [theme.breakpoints.down("md")]: {
    width: "100%",
  },
}));

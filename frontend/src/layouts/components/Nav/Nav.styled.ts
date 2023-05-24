import { Slide, styled } from "@mui/material";
import { NavLink } from "react-router-dom";

export const NavEntry = styled(NavLink)(({ theme }) => ({
  padding: theme.spacing(2, 0),
  textDecorationLine: "none",
  justifyContent: "center",
  marginRight: "16px",
  alignItems: "center",
  borderBottom: "2px solid transparent",
  color: theme.palette.text.primary,
  "&.active": {
    borderBottom: "2px solid #42A7C2",
    color: theme.palette.primary.main,
    fontWeight: 600,
  },
}));

export const SlideNav = styled(Slide)(({ theme }) => ({
  zIndex: theme.zIndex.appBar + 1,
  [theme.breakpoints.up("md")]: {
    position: "static",
    visibility: "visible",
  },
}));

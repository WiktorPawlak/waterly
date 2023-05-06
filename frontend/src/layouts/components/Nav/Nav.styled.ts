import { Slide, styled } from "@mui/material";
import { NavLink } from "react-router-dom";

export const NavEntry = styled(NavLink)(({ theme }) => ({
  padding: theme.spacing(2, 0),
  //paddingBottom: `calc(${theme.spacing(3.3125)} - 4px)`,
  //width: "max-content",
  textDecorationLine: "none",
  justifyContent: "center",
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

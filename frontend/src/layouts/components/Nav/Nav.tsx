import {
  AppBar,
  Box,
  Button,
  Drawer,
  Popover,
  Toolbar,
  Typography,
  useMediaQuery,
  useScrollTrigger,
  useTheme,
} from "@mui/material";
import TranslateIcon from "@mui/icons-material/Translate";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { NavEntry, SlideNav } from "./Nav.styled";
import { Twirl } from "hamburger-react";
import { ProfileCard } from "../ProfileCard";
import DarkModeIcon from "@mui/icons-material/DarkMode";
import { useAccount } from "../../../hooks/useAccount";
import { PATHS } from "../../../routing/paths";

interface NavProps {
  hideMenuEntries?: boolean;
  window?: () => Window;
}

type NavRouteType = {
  path: string;
  name?: string | null;
};

interface NavEntryProps {
  to: string;
  name: string;
}

export type NavEntriesProps = {
  routes: NavRouteType[] | undefined;
};

const NavEntryWithDelay = ({ to, name }: NavEntryProps) => {
  useEffect(() => {
    const delayRedirect = setTimeout(() => {}, 2000);

    return () => {
      clearTimeout(delayRedirect);
    };
  }, [to]);

  return (
    <NavEntry to={to} sx={{ mr: 2 }}>
      {name}
    </NavEntry>
  );
};

const NavEntries = ({ routes }: NavEntriesProps) => (
  <>
    {routes?.map((route) => (
      <NavEntryWithDelay
        key={route.path}
        to={route.path}
        name={route?.name || ""}
      />
    ))}
  </>
);

export const Nav = ({
  hideMenuEntries,
  window,
  routes,
}: NavProps & NavEntriesProps) => {
  const { i18n } = useTranslation();
  const theme = useTheme();
  const { t } = useTranslation();
  const { logout } = useAccount();

  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));

  const preferredLanguage = localStorage.getItem("preferredLanguage") ?? "pl";
  const preferredTheme = localStorage.getItem("themeMode") ?? "light";

  const handleLanguageChange = (lng: string) => {
    localStorage.setItem("preferredLanguage", lng);
    i18n.changeLanguage(lng);
  };
  const handleThemeChange = (preferedThemeMode: string) => {
    localStorage.setItem("themeMode", preferedThemeMode);
    location.reload();
  };

  const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>(
    null
  );
  const [anchorElTheme, setAnchorElTheme] =
    React.useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClickTheme = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorElTheme(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleCloseTheme = () => {
    setAnchorElTheme(null);
  };

  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const open = Boolean(anchorEl);

  const openTheme = Boolean(anchorElTheme);

  const trigger = useScrollTrigger({
    target: window ? window() : undefined,
  });

  const user = localStorage.getItem("user")
    ? JSON.parse(localStorage.getItem("user") || "{}")
    : {};

  return (
    <SlideNav appear={false} direction="down" in={!trigger || isMobileMenuOpen}>
      <AppBar
        position="fixed"
        sx={{
          width: "100%",
          display: "flex",
          justifyContent: "space-between",
          backgroundColor: "background.default",
          boxShadow: "none",
        }}
      >
        <Toolbar
          sx={{
            width: "auto",
            justifyContent: "space-between",
          }}
        >
          <Link to="/">
            <Typography
              variant="h6"
              sx={{ fontWeight: "700", color: "primary.main" }}
            >
              Waterly💧
            </Typography>
          </Link>
          {!hideMenuEntries && (
            <>
              {isMobileWidth && (
                <Twirl
                  toggled={isMobileMenuOpen}
                  toggle={setIsMobileMenuOpen}
                  color={theme.palette.common.black}
                  size={30}
                />
              )}
              <Drawer
                anchor="top"
                open={isMobileWidth && isMobileMenuOpen}
                sx={(theme) => ({
                  zIndex: theme.zIndex.appBar,
                  backgroundColor: "background.default",
                })}
              >
                <Box sx={{ height: "calc(100vh - 65px)", marginTop: "65px" }}>
                  <Toolbar
                    disableGutters
                    sx={{
                      flexDirection: "column",
                      height: "100%",
                      paddingY: 12,
                      rowGap: 2,
                      backgroundColor: "background.default",
                    }}
                  >
                    {user.username ? (
                      <>
                        <NavEntries routes={routes} />
                        <ProfileCard onCLick={logout} />
                      </>
                    ) : (
                      <>
                        <NavEntry to={PATHS.HOME}>
                          {t("navigation.home")}
                        </NavEntry>
                        <NavEntry to={PATHS.LOGIN}>
                          {t("navigation.login")}
                        </NavEntry>
                        <NavEntry to={PATHS.REGISTER}>
                          {t("navigation.register")}
                        </NavEntry>
                      </>
                    )}
                  </Toolbar>
                </Box>
              </Drawer>
              {!isMobileWidth && (
                <Box sx={{ display: "flex", flexDirection: "row" }}>
                  {user.username ? (
                    <>
                      <NavEntries routes={routes} />
                      <ProfileCard onCLick={logout} />
                    </>
                  ) : (
                    <>
                      <NavEntry to={PATHS.HOME}>
                        {t("navigation.home")}
                      </NavEntry>
                      <NavEntry to={PATHS.LOGIN}>
                        {t("navigation.login")}
                      </NavEntry>
                      <Link to={PATHS.REGISTER}>
                        <Button
                          variant="contained"
                          sx={{ textTransform: "none", mt: "10px" }}
                        >
                          {t("navigation.register")}
                        </Button>
                      </Link>
                    </>
                  )}
                  <Button onClick={handleClick}>
                    <TranslateIcon />
                  </Button>
                  <Popover
                    open={open}
                    anchorEl={anchorEl}
                    onClose={handleClose}
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "left",
                    }}
                    transformOrigin={{
                      vertical: "top",
                      horizontal: "left",
                    }}
                  >
                    <Box
                      sx={{
                        display: "flex",
                        flexDirection: "column",
                      }}
                    >
                      <Button
                        onClick={() => handleLanguageChange("en")}
                        variant={
                          preferredLanguage === "en" ? "contained" : "outlined"
                        }
                      >
                        EN
                      </Button>
                      <Button
                        onClick={() => handleLanguageChange("pl")}
                        variant={
                          preferredLanguage === "pl" ? "contained" : "outlined"
                        }
                      >
                        PL
                      </Button>
                    </Box>
                  </Popover>
                  <Button onClick={handleClickTheme}>
                    <DarkModeIcon />
                  </Button>
                  <Popover
                    open={openTheme}
                    anchorEl={anchorElTheme}
                    onClose={handleCloseTheme}
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "left",
                    }}
                    transformOrigin={{
                      vertical: "top",
                      horizontal: "left",
                    }}
                  >
                    <Box
                      sx={{
                        display: "flex",
                        flexDirection: "column",
                      }}
                    >
                      <Button
                        onClick={() => handleThemeChange("light")}
                        variant={
                          preferredTheme === "light" ? "contained" : "outlined"
                        }
                      >
                        {t("theme.light")}
                      </Button>
                      <Button
                        onClick={() => handleThemeChange("dark")}
                        variant={
                          preferredTheme === "dark" ? "contained" : "outlined"
                        }
                      >
                        {t("theme.dark")}
                      </Button>
                    </Box>
                  </Popover>
                </Box>
              )}
            </>
          )}
        </Toolbar>
      </AppBar>
    </SlideNav>
  );
};

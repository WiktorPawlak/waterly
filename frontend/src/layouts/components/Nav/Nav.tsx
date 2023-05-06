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
import React, { useState } from "react";
import { NavEntry, SlideNav } from "./Nav.styled";
import { Twirl } from "hamburger-react";

interface NavProps {
  hideMenuEntries?: boolean;
  window?: () => Window;
}

export const Nav = ({ hideMenuEntries, window }: NavProps) => {
  const { i18n } = useTranslation();
  const theme = useTheme();
  const { t } = useTranslation();

  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));

  const preferredLanguage = localStorage.getItem("preferredLanguage") ?? "pl";

  const handleLanguageChange = (lng: string) => {
    localStorage.setItem("preferredLanguage", lng);
    i18n.changeLanguage(lng);
  };

  const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>(
    null
  );

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const open = Boolean(anchorEl);

  const trigger = useScrollTrigger({
    target: window ? window() : undefined,
  });
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
                    <NavEntry to="/">{t("navigation.login")}</NavEntry>
                    <NavEntry to="/register">
                      {t("navigation.register")}
                    </NavEntry>
                  </Toolbar>
                </Box>
              </Drawer>
              {!isMobileWidth && (
                <Box>
                  <NavEntry to="/">{t("navigation.login")}</NavEntry>
                  <Link to="/register">
                    <Button
                      variant="contained"
                      sx={{ textTransform: "none", ml: 4 }}
                    >
                      {t("navigation.register")}
                    </Button>
                  </Link>
                  <Button onClick={handleClick}>
                    <TranslateIcon />
                  </Button>
                  <Popover
                    open={open}
                    anchorEl={anchorEl}
                    onClose={handleClose}
                    anchorOrigin={{
                      vertical: "center",
                      horizontal: "center",
                    }}
                    transformOrigin={{
                      vertical: "center",
                      horizontal: "center",
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
                </Box>
              )}
            </>
          )}
        </Toolbar>
      </AppBar>
    </SlideNav>
  );
};

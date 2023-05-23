import { Box, Button, Popover, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import React, { useState } from "react";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import RemoveRedEyeOutlinedIcon from "@mui/icons-material/RemoveRedEyeOutlined";
import LogoutOutlinedIcon from "@mui/icons-material/LogoutOutlined";
import { RolesEnum, statusClasses } from "../../../types";

interface ProfileCardProps {
  onCLick: () => void;
}

export const ProfileCard = ({ onCLick }: ProfileCardProps) => {
  const { t } = useTranslation();

  const [isExpanded, setIsExpanded] = useState(false);
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setIsExpanded(!isExpanded);
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);

  const user = localStorage.getItem("user")
    ? JSON.parse(localStorage.getItem("user") || "{}")
    : {};

  const userRoles = user.roles.sort(
    (a: string, b: string) =>
      RolesEnum[a as keyof typeof RolesEnum] -
      RolesEnum[b as keyof typeof RolesEnum]
  );

  const highestRole = userRoles[0];
  const statusClass = statusClasses[highestRole as keyof typeof statusClasses];
  const color = statusClass ? statusClass.color : "";

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "row",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: color,
        borderRadius: "16px",
        padding: "4px",
      }}
    >
      <AccountCircleIcon
        sx={{ color: "primary.contrastText", fontSize: "30px", mr: 1.5 }}
      />
      <Box>
        <Typography
          sx={{
            fontSize: "14px",
            fontWeight: "700",
            color: "primary.contrastText",
          }}
        >
          {user.username}
        </Typography>
        <Typography sx={{ fontSize: "14px", color: "primary.contrastText" }}>
          {t("general.role")}
          {userRoles[0]}
        </Typography>
      </Box>
      <Button onClick={handleClick}>
        <ExpandMoreIcon
          sx={{
            color: "primary.contrastText",
            fontSize: "20px",
            ml: 1.5,
            transition: "transform 0.2s ease-in-out",
            transform: isExpanded ? "rotate(180deg)" : "",
          }}
        />
      </Button>
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "center",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "center",
        }}
      >
        <Box
          sx={{
            display: "flex",
            padding: "20px",
            flexDirection: "column",
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
              mb: 3,
            }}
          >
            <AccountCircleIcon
              sx={{ color: "primary.main", fontSize: "30px", mr: 1.5 }}
            />
            <Box>
              <Typography sx={{ fontSize: "14px", fontWeight: "700" }}>
                {user.username}
              </Typography>
              <Typography sx={{ color: "text.secondary", fontSize: "14px" }}>
                {t("general.role")}
                {userRoles[0]}
              </Typography>
            </Box>
          </Box>
          <Box sx={{ display: "flex", flexDirection: "column" }}>
            <Link to="/profile">
              <Button sx={{ textTransform: "none" }}>
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    color: "text.secondary",
                  }}
                >
                  <RemoveRedEyeOutlinedIcon />
                  <Typography
                    sx={{
                      color: "text.secondary",
                      fontSize: "14px",
                      fontWeight: "500",
                      ml: 1,
                    }}
                  >
                    {t("general.showProfile")}
                  </Typography>
                </Box>
              </Button>
            </Link>
            <Button
              sx={{
                textTransform: "none",
                justifyContent: "flex-start",
                alignItems: "flex-start",
                display: "flex",
              }}
              onClick={onCLick}
            >
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "flex-start",
                  alignItems: "flex-start",
                  color: "text.secondary",
                }}
              >
                <LogoutOutlinedIcon />
                <Typography
                  sx={{
                    color: "text.secondary",
                    fontSize: "14px",
                    fontWeight: "500",
                    ml: 1,
                  }}
                >
                  {t("general.logout")}
                </Typography>
              </Box>
            </Button>
          </Box>
        </Box>
      </Popover>
    </Box>
  );
};

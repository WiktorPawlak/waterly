import React, { useState } from "react";
import Button from "@mui/material/Button";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import {
  AccountActiveStatusDto,
  changeAccountActiveStatus,
} from "../../../api/accountApi";
import { useSnackbar } from "notistack";
import { useTranslation } from "react-i18next";
import { Popup } from "../Popup";
import { Box, Typography } from "@mui/material";
import { resolveApiError } from "../../../api/apiErrors";

export interface LockProps {
  accountId: any;
  active: boolean;
}

export const Lock = ({ accountId, active }: LockProps) => {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const [isActive, setIsActive] = useState(active);

  const handleLockClick = async () => {
    const accountActiveStatusDto: AccountActiveStatusDto = {
      active: !active,
    };
    setAnchorEl(null);
    await changeAccountActiveStatus(accountId, accountActiveStatusDto).then(
      (response) => {
        if (response.status === 200) {
          if (!isActive) {
            enqueueSnackbar(t("manageUsersPage.toastActivateSuccess"), {
              variant: "success",
            });
          } else {
            enqueueSnackbar(t("manageUsersPage.toastDeactivateSuccess"), {
              variant: "success",
            });
          }
          setIsActive(!isActive);
        } else {
          enqueueSnackbar(t(resolveApiError(response.error)), {
            variant: "error",
          });
          console.error(response.error);
        }
      }
    );
  };

  const renderLockIcon = () => {
    if (isActive) {
      return <LockOpenIcon sx={{ color: "green" }} />;
    } else {
      return <LockOutlinedIcon sx={{ color: "red" }} />;
    }
  };

  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);

  const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handlePopoverClose = () => {
    setAnchorEl(null);
  };

  const isOpen = Boolean(anchorEl);

  return (
    <>
      <Button variant="text" onClick={handlePopoverOpen}>
        {renderLockIcon()}
      </Button>
      <Popup
        isOpen={isOpen}
        anchorEl={anchorEl}
        onClose={handlePopoverClose}
        popoverContent={
          <>
            {isActive ? (
              <Typography>{t("popup.lock")}</Typography>
            ) : (
              <Typography>{t("popup.unlock")}</Typography>
            )}
            <Box
              sx={{ display: "flex", justifyContent: "space-between", mt: 2 }}
            >
              <Button variant="outlined" onClick={handlePopoverClose}>
                {t("popup.no")}
              </Button>
              <Button variant="contained" onClick={handleLockClick}>
                {t("popup.yes")}
              </Button>
            </Box>
          </>
        }
      />
    </>
  );
};

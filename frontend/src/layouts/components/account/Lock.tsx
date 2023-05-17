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

export interface LockProps {
  accountId: any;
}

export const Lock = ({ accountId }: LockProps) => {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const [isLocked, setIsLocked] = useState(false);

  const handleLockClick = async () => {
    const accountActiveStatusDto: AccountActiveStatusDto = {
      active: !isLocked,
    };
    await changeAccountActiveStatus(accountId, accountActiveStatusDto).then(
      (response) => {
        if (response.status === 200) {
          if (isLocked) {
            enqueueSnackbar(t("manageUsersPage.toastActivateSuccess"), {
              variant: "success",
            });
          } else {
            enqueueSnackbar(t("manageUsersPage.toastDeactivateSuccess"), {
              variant: "success",
            });
          }
          setIsLocked(!isLocked);
          setAnchorEl(null);
        } else {
          enqueueSnackbar(t("manageUsersPage.toastFailure"), {
            variant: "error",
          });
          console.error(response.error);
        }
      }
    );
  };

  const renderLockIcon = () => {
    if (isLocked) {
      return <LockOutlinedIcon sx={{ color: "red" }} />;
    } else {
      return <LockOpenIcon sx={{ color: "green" }} />;
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
            {isLocked ? (
              <Typography>{t("popup.unlock")}</Typography>
            ) : (
              <Typography>{t("popup.lock")}</Typography>
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

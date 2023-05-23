import React, { useState } from "react";
import Button from "@mui/material/Button";
import {
    acceptAccount,
  changeAccountActiveStatus,
  rejectAccount,
} from "../../../api/accountApi";
import { useSnackbar } from "notistack";
import { useTranslation } from "react-i18next";
import { Popup } from "../Popup";
import { Box, Typography } from "@mui/material";
import { resolveApiError } from "../../../api/apiErrors";
import CheckIcon from "@mui/icons-material/Check";
import ClearIcon from "@mui/icons-material/Clear";

export interface Props {
  accountId: any;
  shouldFetchData: boolean;
  setShouldFetchData: (isOpen: boolean) => void;
  accept: boolean;
}

export const AcceptOrRejectAccount = ({ accountId, shouldFetchData, setShouldFetchData, accept }: Props) => {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();

  const handleSubmit = async () => {
    setAnchorEl(null);
    if(accept) {
        await acceptAccount(accountId).then(
            (response) => {
                if (response.status === 200) {
                    enqueueSnackbar(t("verifyUsersFMPage.toastAcceptSuccess"), {
                        variant: "success",
                      });
                } else {
                    enqueueSnackbar(t(resolveApiError(response.error)), {
                        variant: "error",
                      });
                }
                setShouldFetchData(!shouldFetchData);
            }
        )
    } else {
        await rejectAccount(accountId).then(
            (response) => {
                if (response.status === 200) {
                    enqueueSnackbar(t("verifyUsersFMPage.toastRejectSuccess"), {
                        variant: "success",
                      });
                } else {
                    enqueueSnackbar(t(resolveApiError(response.error)), {
                        variant: "error",
                      });
                }
            }
        )
        setShouldFetchData(!shouldFetchData);
    }
  };

  const renderLockIcon = () => {
    if (accept) {
      return <CheckIcon sx={{ color: "green" }} />
    } else {
      return <ClearIcon sx={{ color: "red" }} />
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
            {accept ? (
              <Typography>{t("popup.accept")}</Typography>
            ) : (
              <Typography>{t("popup.reject")}</Typography>
            )}
            <Box
              sx={{ display: "flex", justifyContent: "space-between", mt: 2 }}
            >
              <Button variant="outlined" onClick={handlePopoverClose}>
                {t("popup.no")}
              </Button>
              <Button variant="contained" onClick={handleSubmit}>
                {t("popup.yes")}
              </Button>
            </Box>
          </>
        }
      />
    </>
  );
};

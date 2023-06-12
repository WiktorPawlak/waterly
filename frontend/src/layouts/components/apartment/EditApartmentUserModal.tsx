import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Tooltip,
  tooltipClasses,
  TooltipProps,
} from "@mui/material";
import { Box, styled } from "@mui/system";
import { useTranslation } from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";
import { enqueueSnackbar } from "notistack";
import ErrorIcon from "@mui/icons-material/Error";
import { ApartmentDto, changeApartmentOwner } from "../../../api/apartmentApi";
import { useState } from "react";
import { OwnerAccountsSelect } from "../account/OwnersAccountsSelect";
import { resolveApiError } from "../../../api/apiErrors";

const ErrorTooltip = styled(({ className, ...props }: TooltipProps) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(({ theme }) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: "#ff3333",
    color: "white",
    fontSize: 15,
    padding: "10px",
  },
}));

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  apartment?: ApartmentDto;
}

export const EditApartmentUserModal = ({
  isOpen,
  setIsOpen,
  apartment,
}: Props) => {
  const { t } = useTranslation();
  const [ownerId, setOwnerId] = useState<number | undefined>();

  const handleClose = () => {
    setIsOpen(false);
    setTimeout(() => setOwnerId(undefined), 1000);
  };

  const handleFormSubmit = async () => {
    if (!ownerId) return;

    const response = await changeApartmentOwner(apartment!!.id, ownerId);

    if (response.status === 200) {
      enqueueSnackbar(t("apartmentPage.ownerChangedSuccessfully"), {
        variant: "success",
      });
      handleClose();
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  return (
    <Box
      sx={{
        flexDirection: "column",
        display: "flex",
        width: "60%",
      }}
    >
      <Box
        sx={{
          mt: { xs: 40, md: 0 },
          display: "flex",
          flexDirection: "column",
          alignItems: { xs: "center", md: "flex-start" },
          justifyContent: { xs: "center", md: "flex-start" },
        }}
      ></Box>
      <Box>
        <Dialog open={isOpen} onClose={handleClose}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <DialogTitle id="role-modal-title">
              {t("changeApartmentOwnerModal.editOwner")}
            </DialogTitle>
            <Button sx={{ width: "30px" }} onClick={handleClose}>
              <CloseIcon />
            </Button>
          </Box>
          <DialogContent sx={{ width: "400px" }}>
            <Box sx={{ width: "100%" }}>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: { xs: "column", md: "row" },
                }}
              ></Box>
              <OwnerAccountsSelect
                setOwnerId={setOwnerId}
                ownerId={ownerId}
                defaultOwnerId={apartment?.ownerId}
              />
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>{t("common.close")}</Button>

            <ErrorTooltip
              placement="top"
              title={
                !ownerId ? (
                  <>
                    <ErrorIcon /> {t("apartmentPage.validation.selectedOwner")}
                  </>
                ) : (
                  ""
                )
              }
            >
              <span>
                <Button
                  disabled={!ownerId}
                  variant="contained"
                  color="primary"
                  onClick={handleFormSubmit}
                >
                  {t("common.confirm")}
                </Button>
              </span>
            </ErrorTooltip>
          </DialogActions>
        </Dialog>
      </Box>
    </Box>
  );
};

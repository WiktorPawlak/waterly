import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import { resendEmailEditMail } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import { useTranslation } from "react-i18next";
import { useSnackbar } from "notistack";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const ResendEditMailDialog = ({ isOpen, setIsOpen }: Props) => {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();

  const handleResendEmail = async () => {
    const response = await resendEmailEditMail();

    if (response.status === 200) {
      enqueueSnackbar(t("editAccountDetailsPage.alert.emailSuccesResended"), {
        variant: "success",
      });
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  const handleClose = () => {
    setIsOpen(false);
    window.location.reload();
  };

  return (
    <>
      <Dialog
        open={isOpen}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {t("editAccountDetailsPage.resendAcceptMail.modalHeader")}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {t("editAccountDetailsPage.resendAcceptMail.modalBody")}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" onClick={handleResendEmail}>
            {t("editAccountDetailsPage.resendAcceptMail.resendButton")}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

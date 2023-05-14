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
import { Toast } from "../Toast";
import { useToast } from "../../../hooks/useToast";
import { useTranslation } from "react-i18next";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const ResendEditMailDialog = ({ isOpen, setIsOpen }: Props) => {
  const { t } = useTranslation();
  const toast = useToast();

  const handleResendEmail = async () => {
    const response = await resendEmailEditMail();

    if (response.status === 200) {
      toast.showSuccessToast(
        t("editAccountDetailsPage.alert.emailSuccesResended")
      );
    } else {
      toast.showErrorToast(t(resolveApiError(response.error)));
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
      <Toast
        isToastOpen={toast.isToastOpen}
        setIsToastOpen={toast.setIsToastOpen}
        message={toast.message}
        severity={toast.severity}
      />
    </>
  );
};

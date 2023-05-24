import { Box, Button, Divider, Modal, Typography } from "@mui/material";
import { resendEmailEditMail } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import { useTranslation } from "react-i18next";
import { enqueueSnackbar } from "notistack";

const style = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  border: "2px solid #000",
  boxShadow: 24,
  p: 4,
};

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const ResendEditMailModal = ({ isOpen, setIsOpen }: Props) => {
  const { t } = useTranslation();

  const handleResendEmail = async () => {
    const response = await resendEmailEditMail();

    if (response.status === 200) {
      enqueueSnackbar(t("editAccountDetailsPage.alert.detailsSuccesEdited"), {
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
      <Modal
        open={isOpen}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <Typography id="modal-modal-title" variant="h6" component="h2">
            {t("editAccountDetailsPage.resendAcceptMail.modalHeader")}
          </Typography>
          <Divider variant="middle" sx={{ my: 2 }} />
          <Typography id="modal-modal-description" sx={{ mt: 2 }}>
            {t("editAccountDetailsPage.resendAcceptMail.modalBody")}
          </Typography>
          <Divider variant="middle" sx={{ my: 2 }} />
          <Button variant="contained" onClick={handleResendEmail}>
            {t("editAccountDetailsPage.resendAcceptMail.resendButton")}
          </Button>
        </Box>
      </Modal>
    </>
  );
};

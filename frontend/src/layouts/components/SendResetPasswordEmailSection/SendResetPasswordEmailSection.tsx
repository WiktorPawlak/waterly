import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { resetPasswordEmailSchema } from "../../../validation/validationSchemas";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  TextField,
  DialogActions,
  Button,
  Box,
} from "@mui/material";
import React, { useState } from "react";
import { postSendResetPasswordEmail } from "../../../api/accountApi";
import { useToast } from "../../../hooks/useToast";
import { useTranslation } from "react-i18next";
import { Toast } from "../Toast";
import { resolveApiError } from "../../../api/apiErrors";

export const SendResetPasswordEmailSection = () => {
  const {
    register: registerEmail,
    handleSubmit: handleEmailSubmit,
    formState: { errors: emailErrors },
  } = useForm<resetPasswordEmailSchema>({
    resolver: zodResolver(resetPasswordEmailSchema),
  });

  const emailErrorMessage = emailErrors?.email?.message;
  const [open, setOpen] = React.useState(false);
  const [email, setEmail] = useState("");
  const toast = useToast();
  const { t } = useTranslation();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  async function postSendResetPasswordEmailHandle() {
    const response = await postSendResetPasswordEmail(email);
    if (response.status === 200) {
      handleClose();
      toast.showSuccessToast(t("resetEmailSuccess.alert.emailSent"));
    } else {
      toast.showErrorToast(t(resolveApiError(response.error)));
    }
  }

  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "flex-end",
        justifyContent: "flex-end",
      }}
    >
      <Button
        variant="text"
        sx={{
          textTransform: "none",
          width: "204px",
          justifyContent: "flex-end",
          alignSelf: "flex-end",
          mb: { xs: 3, md: 6 },
        }}
        onClick={handleClickOpen}
      >
        {t("logInPage.form.forgotPasswordLinkLabel")}
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{t("logInPage.form.forgotPasswordLinkLabel")}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {t("logInPage.form.forgotPasswordDescription")}
          </DialogContentText>
          <TextField
            {...registerEmail("email")}
            error={!!emailErrorMessage}
            helperText={emailErrorMessage && t(emailErrorMessage)}
            onChange={(e) => setEmail(e.target.value)}
            autoFocus
            margin="dense"
            label={t("registerPage.form.emailLabel")}
            type="email"
            fullWidth
            variant="standard"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>
            {t("logInPage.form.forgotPasswordCancel")}
          </Button>
          <Button
            variant="contained"
            disabled={email.length === 0}
            onClick={handleEmailSubmit(async () => {
              await postSendResetPasswordEmailHandle();
            })}
          >
            {t("logInPage.form.forgotPasswordSend")}
          </Button>
        </DialogActions>
      </Dialog>
      <Toast
        isToastOpen={toast.isToastOpen}
        setIsToastOpen={toast.setIsToastOpen}
        message={toast.message}
        severity={toast.severity}
      />
    </Box>
  );
};

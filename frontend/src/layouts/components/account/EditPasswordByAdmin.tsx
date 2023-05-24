import { useTranslation } from "react-i18next";
import { useState } from "react";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import {
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  TextField,
  InputAdornment,
  IconButton,
  DialogActions,
} from "@mui/material";
import { Box } from "@mui/system";
import { postChangePasswordByAdmin } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import {
  ChangePasswordByAdminSchema,
  changePasswordByAdminSchema,
} from "../../../validation/validationSchemas";
import { enqueueSnackbar } from "notistack";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  email: string;
}

export const EditPasswordByAdmin = ({ isOpen, setIsOpen, email }: Props) => {
  const { t } = useTranslation();
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ChangePasswordByAdminSchema>({
    resolver: zodResolver(changePasswordByAdminSchema),
  });

  const newPasswordErrorMessage = errors?.newPassword?.message;
  const confirmedPasswordErrorMessage = errors?.confirmPassword?.message;

  const handleClickShowConfirmPassword = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleClickShowNewPassword = () => {
    setShowNewPassword(!showNewPassword);
  };

  const handleMouseDownNewPassword = () => {
    setShowNewPassword(!showNewPassword);
  };

  const handleMouseDownConfirmPassword = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleClose = () => {
    setIsOpen(false);
    setNewPassword("");
    setConfirmPassword("");
  };

  async function postChangePasswordByAdminHandle() {
    const passwordChangeByAdminDto = {
      newPassword: newPassword,
    };
    const response = await postChangePasswordByAdmin(
      email,
      passwordChangeByAdminDto
    );
    if (response.status === 200) {
      enqueueSnackbar(t("editPasswordByAdminDialog.success"), {
        variant: "success",
      });
      handleClose();
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  }

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        width: "100%",
      }}
    >
      <Dialog open={isOpen} onClose={handleClose}>
        <DialogTitle>{t("editPasswordByAdminDialog.header")}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {t("editPasswordByAdminDialog.description")}
          </DialogContentText>
          <TextField
            {...register("newPassword")}
            error={!!newPasswordErrorMessage}
            helperText={newPasswordErrorMessage && t(newPasswordErrorMessage)}
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            autoFocus
            margin="dense"
            label={t("changePassword.passwords.newPasswordLabel")}
            type={showNewPassword ? "text" : "password"}
            fullWidth
            variant="standard"
            sx={{ mb: { md: 2 } }}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    aria-label="toggle password visibility"
                    onClick={handleClickShowNewPassword}
                    onMouseDown={handleMouseDownNewPassword}
                  >
                    {showNewPassword ? <Visibility /> : <VisibilityOff />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
          <TextField
            {...register("confirmPassword")}
            error={!!confirmedPasswordErrorMessage}
            helperText={
              confirmedPasswordErrorMessage && t(confirmedPasswordErrorMessage)
            }
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            autoFocus
            margin="dense"
            label={t("changePassword.passwords.confirmNewPasswordLabel")}
            type={showConfirmPassword ? "text" : "password"}
            fullWidth
            variant="standard"
            sx={{ mb: { md: 2 } }}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    aria-label="toggle password visibility"
                    onClick={handleClickShowConfirmPassword}
                    onMouseDown={handleMouseDownConfirmPassword}
                  >
                    {showConfirmPassword ? <Visibility /> : <VisibilityOff />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>
            {t("changePassword.buttons.cancel")}
          </Button>
          <Button
            disabled={newPassword.length === 0 || confirmPassword.length === 0}
            onClick={handleSubmit(async () => {
              await postChangePasswordByAdminHandle();
            })}
          >
            {t("changePassword.buttons.change")}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

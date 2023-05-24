import {
  Box,
  Button,
  IconButton,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useSearchParams } from "react-router-dom";
import { postResetPassword } from "../../../api/accountApi";
import {
  resetPasswordSchema,
  resetPasswordSchemaType,
} from "../../../validation/validationSchemas";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { enqueueSnackbar } from "notistack";

export const ResetPasswordFormSection = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const token = searchParams.get("token") as string;
  const navigation = useNavigate();
  const { t } = useTranslation();
  const [newPassword, setNewPassword] = useState("");
  const [newPasswordRepeat, setNewPasswordRepeat] = useState("");

  const [showPasswordRepeat, setShowPasswordRepeat] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<resetPasswordSchemaType>({
    resolver: zodResolver(resetPasswordSchema),
  });

  const passwordErrorMessage = errors?.password?.message;
  const passwordRepeatMessage = errors?.confirmPassword?.message;

  const handleClickShowNewPasswordRepeat = () => {
    setShowPasswordRepeat(!showPasswordRepeat);
  };
  const handleClickShowNewPassword = () => {
    setShowNewPassword(!showNewPassword);
  };
  const handleMouseDownNewPasswordRepeat = () => {
    setShowPasswordRepeat(!showPasswordRepeat);
  };
  const handleMouseDownNewPassword = () => {
    setShowNewPassword(!showNewPassword);
  };

  async function postResetPasswordHandle() {
    const passwordResetDto = {
      token,
      newPassword,
    };

    const response = await postResetPassword(passwordResetDto);
    if (response.status === 200) {
      enqueueSnackbar(t("resetPassword.alert.passwordReseted"), {
        variant: "success",
      });
      setTimeout(() => {
        navigation("/");
      }, 3000);
    } else {
      enqueueSnackbar(t("resetPassword.alert.passwordNotReseted"), {
        variant: "error",
      });
    }
  }

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
          display: "flex",
          flexDirection: "column",
          alignItems: { xs: "center", md: "flex-start" },
          justifyContent: { xs: "center", md: "flex-start" },
        }}
      >
        <Typography
          variant="h2"
          sx={{
            fontSize: { xs: "32px", md: "40px" },
            fontWeight: "700",
            mb: { xs: 2, md: 3 },
          }}
        >
          {t("resetPassword.header.title")}
        </Typography>
        <Typography
          variant="body1"
          sx={{
            fontSize: "20px",
            fontWeight: "500",
            color: "gray",
            mb: { xs: 3, md: 6 },
          }}
        >
          {t("resetPassword.header.description")}
        </Typography>
      </Box>
      <TextField
        {...register("password")}
        error={!!passwordErrorMessage}
        helperText={passwordErrorMessage && t(passwordErrorMessage)}
        label={t("resetPassword.form.newPasswordLabel")}
        type={showNewPassword ? "text" : "password"}
        variant="standard"
        onChange={(e) => setNewPassword(e.target.value)}
        sx={{
          mb: 3,
          "& label": {
            color: "text.secondary",
          },
          "& label.Mui-focused": {
            color: "primary.main",
          },
        }}
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
        error={!!passwordRepeatMessage}
        helperText={passwordRepeatMessage && t(passwordRepeatMessage)}
        label={t("resetPassword.form.newPasswordRepeatLabel")}
        type={showPasswordRepeat ? "text" : "password"}
        variant="standard"
        onChange={(e) => setNewPasswordRepeat(e.target.value)}
        sx={{
          mb: 3,
          "& label": {
            color: "text.secondary",
          },
          "& label.Mui-focused": {
            color: "primary.main",
          },
        }}
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                aria-label="toggle password visibility"
                onClick={handleClickShowNewPasswordRepeat}
                onMouseDown={handleMouseDownNewPasswordRepeat}
              >
                {showPasswordRepeat ? <Visibility /> : <VisibilityOff />}
              </IconButton>
            </InputAdornment>
          ),
        }}
      />
      <Button
        variant="contained"
        sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
        onClick={handleSubmit(async () => {
          await postResetPasswordHandle();
        })}
      >
        {t("resetPassword.form.submitPasswordReset")}
      </Button>
    </Box>
  );
};

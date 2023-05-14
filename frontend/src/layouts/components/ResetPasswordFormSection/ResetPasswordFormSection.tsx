import { Box, Button, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useSearchParams } from "react-router-dom";
import { postResetPassword, TokenType} from "../../../api/accountApi";
import { resetPasswordSchema, resetPasswordSchemaType } from "../../../validation/validationSchemas";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useToast } from "../../../hooks/useToast";
import { Toast } from "../Toast";

export const ResetPasswordFormSection = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const token = searchParams.get("token") as string;
    const navigation = useNavigate();
    const toast = useToast();
    const { t } = useTranslation();
    const [newPassword, setNewPassword] = useState("");
    const [newPasswordRepeat, setNewPasswordRepeat] = useState("");

    const {
      register,
      handleSubmit,
      formState: { errors },
    } = useForm<resetPasswordSchemaType>({
      resolver: zodResolver(resetPasswordSchema),
    });

    const passwordErrorMessage = errors?.password?.message;
    const passwordRepeatMessage = errors?.confirmPassword?.message

    async function postResetPasswordHandle() {
      const passwordResetDto = {
        token: token,
        newPassword: newPassword,
        type: TokenType.PasswordReset
      }
      const response = await postResetPassword(passwordResetDto);
      if (response.status === 200) {
        toast.showSuccessToast(t
          ("resetPassword.alert.passwordReseted")
          );
          setTimeout(() => {
            navigation("/");
          }, 3000);
      } else {
        toast.showErrorToast(
          t("resetPassword.alert.passwordNotReseted")
        );
      }
      console.log(passwordResetDto);
    }

    return (
        <Box 
            sx={{
            flexDirection: "column",
            display: "flex",
            width: "60%",
          }}>
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
            type="password"
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
          />
          <TextField
                {...register("confirmPassword")}
                error={!!passwordRepeatMessage}
                helperText={passwordRepeatMessage && t(passwordRepeatMessage)}
                label={t("resetPassword.form.newPasswordRepeatLabel")}
                type="password"
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
              />
          <Button
              variant="contained"
              sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
              onClick={handleSubmit(async () => { await postResetPasswordHandle() })}>
              {t("resetPassword.form.submitPasswordReset")}
          </Button>
          <Toast
          isToastOpen={toast.isToastOpen}
          setIsToastOpen={toast.setIsToastOpen}
          message={toast.message}
          severity={toast.severity}
        />
        </Box>
    );
};
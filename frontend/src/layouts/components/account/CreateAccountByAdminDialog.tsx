import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import { Box } from "@mui/system";
import { useTranslation } from "react-i18next";
import { createAccountByAdmin } from "../../../api/accountApi";
import CloseIcon from "@mui/icons-material/Close";
import {
  accountDetailsSchema,
  AccountDetailsSchemaType,
} from "../../../validation/validationSchemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { languages } from "../../../types";
import { resolveApiError } from "../../../api/apiErrors";
import { enqueueSnackbar } from "notistack";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const CreateAccountByAdminDialog = ({ isOpen, setIsOpen }: Props) => {
  const { t } = useTranslation();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<AccountDetailsSchemaType>({
    resolver: zodResolver(accountDetailsSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      login: "",
      password: "",
      confirmPassword: "",
      email: "",
      firstName: "",
      lastName: "",
      phoneNumber: "",
    },
  });

  const handleClose = () => {
    setIsOpen(false);
  };

  const handleReset = () => {
    reset();
  };

  const {
    email: emailError,
    login: loginError,
    firstName: firstNameError,
    lastName: lastNameError,
    phoneNumber: phoneNumberError,
    password: passwordError,
    confirmPassword: confirmPasswordError,
  } = errors;

  const emailErrorMessage = emailError?.message;
  const loginErrorMessage = loginError?.message;
  const firstNameErrorMessage = firstNameError?.message;
  const lastNameErrorMessage = lastNameError?.message;
  const phoneNumberErrorMessage = phoneNumberError?.message;
  const passwordErrorMessage = passwordError?.message;
  const confirmPasswordErrorMessage = confirmPasswordError?.message;
  const languageTagFromStorage = localStorage.getItem("preferredLanguage");
  const formattedLanguageTag =
    languages[languageTagFromStorage as keyof typeof languages];
  const languageTag = formattedLanguageTag ?? languages.pl;

  const handleFormSubmit = async (formData: AccountDetailsSchemaType) => {
    const registerUserRequest = { ...formData, languageTag };

    const response = await createAccountByAdmin(registerUserRequest);

    if (response.status === 201) {
      enqueueSnackbar(t("createAccountDialog.accountCreatedSuccessfully"), {
        variant: "success",
      });
      setIsOpen(false);
      handleReset();
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
              {t("createAccountDialog.createAccount")}
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
              >
                <TextField
                  label={t("registerPage.form.firstName")}
                  {...register("firstName")}
                  error={!!firstNameErrorMessage}
                  helperText={firstNameErrorMessage && t(firstNameErrorMessage)}
                  variant="standard"
                  name="firstName"
                  sx={{
                    mb: 3,
                    mr: { xs: 0, md: 5 },
                    "& label": {
                      color: "text.secondary",
                    },
                    "& label.Mui-focused": {
                      color: "primary.main",
                    },
                  }}
                />
                <TextField
                  label={t("registerPage.form.lastName")}
                  {...register("lastName")}
                  error={!!lastNameErrorMessage}
                  helperText={lastNameErrorMessage && t(lastNameErrorMessage)}
                  variant="standard"
                  name="lastName"
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
              </Box>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: { xs: "column", md: "row" },
                }}
              >
                <TextField
                  label="Login *"
                  {...register("login")}
                  error={!!loginErrorMessage}
                  helperText={loginErrorMessage && t(loginErrorMessage)}
                  variant="standard"
                  name="login"
                  sx={{
                    mb: 3,
                    mr: { xs: 0, md: 5 },
                    "& label": {
                      color: "text.secondary",
                    },
                    "& label.Mui-focused": {
                      color: "primary.main",
                    },
                  }}
                />
                <TextField
                  label={t("registerPage.form.emailLabel")}
                  {...register("email")}
                  error={!!emailErrorMessage}
                  helperText={emailErrorMessage && t(emailErrorMessage)}
                  variant="standard"
                  name="email"
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
              </Box>
              <TextField
                label={t("registerPage.form.phoneNumber")}
                {...register("phoneNumber")}
                error={!!phoneNumberErrorMessage}
                helperText={
                  phoneNumberErrorMessage && t(phoneNumberErrorMessage)
                }
                name="phoneNumber"
                variant="standard"
                type="number"
                sx={{
                  mb: 3,
                  width: "100%",
                  "& label": {
                    color: "text.secondary",
                  },
                  "& label.Mui-focused": {
                    color: "primary.main",
                  },
                }}
              />
              <Box
                sx={{
                  display: "flex",
                  flexDirection: { xs: "column", md: "row" },
                }}
              >
                <TextField
                  label={t("registerPage.form.passwordLabel")}
                  {...register("password")}
                  error={!!passwordErrorMessage}
                  helperText={passwordErrorMessage && t(passwordErrorMessage)}
                  variant="standard"
                  name="password"
                  type="password"
                  sx={{
                    mb: 3,
                    mr: { xs: 0, md: 5 },
                    "& label": {
                      color: "text.secondary",
                    },
                    "& label.Mui-focused": {
                      color: "primary.main",
                    },
                  }}
                />
                <TextField
                  label={t("registerPage.form.confirmPassword")}
                  {...register("confirmPassword")}
                  error={!!confirmPasswordErrorMessage}
                  helperText={
                    confirmPasswordErrorMessage &&
                    t(confirmPasswordErrorMessage)
                  }
                  variant="standard"
                  type="password"
                  name="confirmPassword"
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
              </Box>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>{t("common.close")}</Button>
            <Button
              variant="contained"
              color="primary"
              onClick={handleSubmit(handleFormSubmit)}
            >
              {t("common.confirm")}
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Box>
  );
};

import {
  Box,
  Button,
  TextField,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import loginPose from "../../../../../assets/loginPose.svg";
import { useUser } from "../../../../../hooks/useUser";
import { languages } from "../../../../../types";
import { useNavigate } from "react-router-dom";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import {
  accountDetailsSchema,
  AccountDetailsSchemaType,
} from "../../../../../validation/validationSchemas";

export const RegisterFormSection = () => {
  const navigation = useNavigate();
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));

  const languageTagFromStorage = localStorage.getItem("preferredLanguage");
  const formattedLanguageTag =
    languages[languageTagFromStorage as keyof typeof languages];
  const languageTag = formattedLanguageTag ?? languages.pl;

  const {
    register,
    handleSubmit,
    formState: { errors },
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

  const { registerUser } = useUser();

  const handleFormSubmit = async (formData: AccountDetailsSchemaType) => {
    const registerUserRequest = { ...formData, languageTag };

    await registerUser(registerUserRequest);
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
      >
        <Typography
          variant="h2"
          sx={{
            fontSize: { xs: "32px", md: "40px" },
            fontWeight: "700",
            mb: { xs: 2, md: 3 },
          }}
        >
          {t("registerPage.header.title")}
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
          {t("registerPage.header.description")}
        </Typography>
      </Box>
      {isMobileWidth && (
        <img
          src={loginPose}
          alt="loginPose"
          style={{ width: "100%", height: "100%", objectFit: "cover" }}
        />
      )}
      <form onSubmit={handleSubmit(handleFormSubmit)}>
        <Box
          sx={{ display: "flex", flexDirection: { xs: "column", md: "row" } }}
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
          sx={{ display: "flex", flexDirection: { xs: "column", md: "row" } }}
        >
          <TextField
            label={t("registerPage.form.emailLabel")}
            {...register("email")}
            error={!!emailErrorMessage}
            helperText={emailErrorMessage && t(emailErrorMessage)}
            variant="standard"
            name="email"
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
            label="Login"
            {...register("login")}
            error={!!loginErrorMessage}
            helperText={loginErrorMessage && t(loginErrorMessage)}
            variant="standard"
            name="login"
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
          helperText={phoneNumberErrorMessage && t(phoneNumberErrorMessage)}
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
          sx={{ display: "flex", flexDirection: { xs: "column", md: "row" } }}
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
              confirmPasswordErrorMessage && t(confirmPasswordErrorMessage)
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
        <Button
          variant="contained"
          type="submit"
          sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
        >
          {t("registerPage.form.submitButtonLabel")}
        </Button>
      </form>
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          mb: 1,
        }}
      >
        <Typography sx={{ fontSize: "14px", color: "text.secondary" }}>
          {t("registerPage.form.accountExistsQuestion")}
        </Typography>
        <Button
          variant="text"
          sx={{ textTransform: "none" }}
          onClick={() => navigation("/")}
        >
          {t("registerPage.form.loginButton")}
        </Button>
      </Box>
    </Box>
  );
};

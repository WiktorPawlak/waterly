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
import { useState } from "react";
import { useUser } from "../../../../../hooks/useUser";
import { languages } from "../../../../../types";

interface RegisterFormValues {
  login: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  confirmPassword: string;
  phoneNumber: string;
  languageTag: string;
}

export const RegisterFormSection = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));

  const { registerUser } = useUser();
  const languageTagFromStorage = localStorage.getItem("preferredLanguage");
  const formattedLanguageTag =
    languages[languageTagFromStorage as keyof typeof languages];

  const [formValues, setFormValues] = useState<RegisterFormValues>({
    login: "",
    password: "",
    confirmPassword: "",
    email: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    languageTag: formattedLanguageTag ?? languages.pl,
  });

  const handleFormSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (formValues.password !== formValues.confirmPassword) {
      console.log("Passwords do not match");
      return;
    } else if (formValues.password === formValues.confirmPassword) {
      const { confirmPassword, ...user } = formValues;

      registerUser(user);
    }
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormValues((prevValues) => ({
      ...prevValues,
      [name]: value,
    }));
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
          alt="XD"
          style={{ width: "100%", height: "100%", objectFit: "cover" }}
        />
      )}
      <form onSubmit={handleFormSubmit}>
        <Box
          sx={{ display: "flex", flexDirection: { xs: "column", md: "row" } }}
        >
          <TextField
            label="First Name"
            variant="standard"
            name="firstName"
            onChange={handleInputChange}
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
            label="Last Name"
            variant="standard"
            name="lastName"
            onChange={handleInputChange}
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
            variant="standard"
            name="email"
            onChange={handleInputChange}
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
            variant="standard"
            name="login"
            onChange={handleInputChange}
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
          label="Phone Number"
          name="phoneNumber"
          variant="standard"
          onChange={handleInputChange}
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
            onChange={handleInputChange}
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
            label="Confirm Password"
            variant="standard"
            onChange={handleInputChange}
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
          variant="text"
          sx={{
            textTransform: "none",
            width: "204px",
            justifyContent: "flex-end",
            alignSelf: "flex-end",
            mb: { xs: 3, md: 6 },
          }}
        >
          {t("registerPage.form.forgotPasswordLinkLabel")}
        </Button>
        <Button
          variant="contained"
          type="submit"
          sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
        >
          {t("registerPage.form.submitButtonLabel")}
        </Button>
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            mb: 1,
          }}
        >
          <Typography sx={{ fontSize: "14px", color: "text.secondary" }}>
            {t("registerPage.form.registerText")}
          </Typography>
          <Button variant="text" sx={{ textTransform: "none" }}>
            {t("registerPage.form.registerButton")}
          </Button>
        </Box>
      </form>
    </Box>
  );
};

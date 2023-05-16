import {
  Box,
  Button,
  TextField,
  Typography,
  useMediaQuery,
  useTheme,
  IconButton,
  InputAdornment,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import loginPose from "../../../../../assets/loginPose.svg";
import { useUser } from "../../../../../hooks/useUser";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  loginSchema,
  LoginSchemaType,
} from "../../../../../validation/validationSchemas";
import { SendResetPasswordEmailSection } from "../../../SendResetPasswordEmailSection/SendResetPasswordEmailSection";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { LoginRequestBody } from "../../../../../api/authApi";

export const LoginFormSection = () => {
  const navigation = useNavigate();
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));

  const {
    register: registerLogin,
    handleSubmit: handleLoginSubmit,
    formState: { errors: loginErrors },
  } = useForm<LoginSchemaType>({
    resolver: zodResolver(loginSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      login: "",
      password: "",
    },
  });
  const loginErrorMessage = loginErrors?.login?.message;
  const passwordErrorMessage = loginErrors?.password?.message;

  const handleFormSubmit = async (credentials: LoginRequestBody) => {
    if (!(await logInClient(credentials))) {
      return <Box>Loading...</Box>;
    }
  };

  const [showPassword, setShowPassword] = useState(false);
  const { logInClient } = useUser();

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };
  const handleMouseDownPassword = () => {
    setShowPassword(!showPassword);
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
          {t("logInPage.header.title")}
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
          {t("logInPage.header.description")}
        </Typography>
      </Box>
      {isMobileWidth && (
        <img
          src={loginPose}
          alt="loginPose"
          style={{ width: "100%", height: "100%", objectFit: "cover" }}
        />
      )}
      <form onSubmit={handleLoginSubmit(handleFormSubmit)}>
        <TextField
          label={t("logInPage.form.loginLabel")}
          {...registerLogin("login")}
          error={!!loginErrorMessage}
          helperText={loginErrorMessage && t(loginErrorMessage)}
          variant="standard"
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
        <TextField
          label={t("logInPage.form.passwordLabel")}
          {...registerLogin("password")}
          error={!!passwordErrorMessage}
          helperText={passwordErrorMessage && t(passwordErrorMessage)}
          type={showPassword ? "text" : "password"}
          variant="standard"
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
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={handleClickShowPassword}
                  onMouseDown={handleMouseDownPassword}
                >
                  {showPassword ? <Visibility /> : <VisibilityOff />}
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
        <SendResetPasswordEmailSection />

        <Button
          variant="contained"
          type="submit"
          sx={{ textTransform: "none", width: "100%", mb: { xs: 3, md: 3 } }}
        >
          {t("logInPage.form.submitButtonLabel")}
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
          {t("logInPage.form.registerText")}
        </Typography>
        <Button
          variant="text"
          sx={{ textTransform: "none" }}
          onClick={() => navigation("/register")}
        >
          {t("logInPage.form.registerButton")}
        </Button>
      </Box>
    </Box>
  );
};

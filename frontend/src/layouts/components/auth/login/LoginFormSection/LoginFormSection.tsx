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
import { useToast } from "../../../../../hooks/useToast";
import { SendResetPasswordEmailSection } from "../../../SendResetPasswordEmailSection/SendResetPasswordEmailSection";
import { Visibility, VisibilityOff } from "@mui/icons-material";

export const LoginFormSection = () => {
  const navigation = useNavigate();
  const { t } = useTranslation();
  const {
    register: registerLogin,
    handleSubmit: handleLoginSubmit,
    formState: { errors: loginErrors },
  } = useForm<LoginSchemaType>({
    resolver: zodResolver(loginSchema),
  });

  const loginErrorMessage = loginErrors?.login?.message;
  const passwordErrorMessage = loginErrors?.password?.message;

  const theme = useTheme();
  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));

  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const { logInClient } = useUser();

  const signInButtonHandle = async () => {
    if (!(await logInClient({ login, password }))) {
      return <Box>Loading...</Box>;
    }
  };

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
      <TextField
        label={t("logInPage.form.loginLabel")}
        {...registerLogin("login")}
        error={!!loginErrorMessage}
        helperText={loginErrorMessage && t(loginErrorMessage)}
        variant="standard"
        onChange={(e) => setLogin(e.target.value)}
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
        label={t("logInPage.form.passwordLabel")}
        {...registerLogin("password")}
        error={!!passwordErrorMessage}
        helperText={passwordErrorMessage && t(passwordErrorMessage)}
        type={showPassword ? "text" : "password"}
        variant="standard"
        onChange={(e) => setPassword(e.target.value)}
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
        sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
        onClick={handleLoginSubmit(signInButtonHandle)}
      >
        {t("logInPage.form.submitButtonLabel")}
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

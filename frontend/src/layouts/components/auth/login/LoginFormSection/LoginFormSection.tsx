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

export const LoginFormSection = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));
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
          alt="XD"
          style={{ width: "100%", height: "100%", objectFit: "cover" }}
        />
      )}
      <TextField
        label={t("logInPage.form.emailLabel")}
        variant="standard"
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
        type="password"
        variant="standard"
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
        variant="text"
        sx={{
          textTransform: "none",
          width: "204px",
          justifyContent: "flex-end",
          alignSelf: "flex-end",
          mb: { xs: 3, md: 6 },
        }}
      >
        {t("logInPage.form.forgotPasswordLinkLabel")}
      </Button>
      <Button
        variant="contained"
        sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
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
        <Button variant="text" sx={{ textTransform: "none" }}>
          {t("logInPage.form.registerButton")}
        </Button>
      </Box>
    </Box>
  );
};

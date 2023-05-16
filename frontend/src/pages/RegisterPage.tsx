import { Box, useMediaQuery, useTheme } from "@mui/material";
import { MainLayout } from "../layouts/MainLayout/MainLayout";
import loginPose from "../assets/loginPose.svg";
import { RegisterFormSection } from "../layouts/components/auth/register/RegisterFormSection/RegisterFormSection";

const RegisterPage = () => {
  const theme = useTheme();
  const isMobileWidth = useMediaQuery(theme.breakpoints.down("md"));
  return (
    <MainLayout>
      <Box
        sx={{
          height: "100vh",
          width: { xs: "100%", md: "auto" },
          placeItems: { xs: "center", md: "start" },
          justifyContent: { xs: "center", md: "start" },
          alignItems: { xs: "center", md: "start" },
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
        }}
      >
        <Box
          sx={{
            width: { xs: "400px", md: "700px" },
            height: "auto",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          {!isMobileWidth && (
            <img
              src={loginPose}
              alt="loginPose"
              style={{
                width: "100%",
                height: "100%",
                objectFit: "cover",
                marginRight: "256px",
              }}
            />
          )}
        </Box>
        <RegisterFormSection />
      </Box>
    </MainLayout>
  );
};

export default RegisterPage;

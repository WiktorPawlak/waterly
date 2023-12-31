import { Box, useMediaQuery, useTheme } from "@mui/material";
import { MainLayout } from "../layouts/MainLayout/MainLayout";
import loginPose from "../assets/loginPose.svg";
import { ResetPasswordFormSection } from "../layouts/components/ResetPasswordFormSection/ResetPasswordFormSection";

const ResetPasswordPage = () => {

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
            <ResetPasswordFormSection />
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
                  alt="loginImage"
                  style={{
                    width: "100%",
                    height: "100%",
                    objectFit: "cover",
                    marginLeft: "256px",
                  }}
                />
              )}
            </Box>
          </Box>
        </MainLayout>
      );
};

export default ResetPasswordPage;
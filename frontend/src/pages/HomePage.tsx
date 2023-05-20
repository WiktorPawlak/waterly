import { useTranslation } from "react-i18next";
import { Box, Button, Typography } from "@mui/material";
import homeImg from "../assets/homeImg.png";
import { MainLayout } from "../layouts/MainLayout";
import { Link } from "react-router-dom";
import { PATHS } from "../routing/paths";
import { useAccount } from "../hooks/useAccount";

const HomePage = () => {
  const { t } = useTranslation();
  const { account } = useAccount();

  return (
    <MainLayout isHomePage={true}>
      <Box
        sx={{
          mt: { xs: 4, md: 0 },
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          justifyContent: "flex-start",
          alignItems: "center",
          overflow: "hidden",
          width: "100%",
          height: "100vh",
          position: { xs: "static", md: "relative" },
        }}
      >
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            width: { xs: "100%", md: "50%" },
          }}
        >
          <Typography
            variant="h2"
            sx={{
              mt: { xs: 4, md: 0 },
              fontSize: { xs: "20px", md: "64px" },
              fontWeight: "700",
            }}
          >
            {t("homePage.title")}
          </Typography>
          <Typography
            sx={{
              mt: 2,
              color: "text.secondary",
              mb: { xs: 4, md: 0 },
              fontSize: { xs: "12px", md: "24px" },
            }}
          >
            {t("homePage.description")}
          </Typography>
          {account === null && (
            <Button
              variant="contained"
              sx={{
                textTransform: "none",
                maxWidth: "150px",
                mt: { xs: 1, md: 3 },
                py: 1,
              }}
              component={Link}
              to={PATHS.LOGIN}
            >
              <Typography sx={{ fontSize: "16px", color: "white" }}>
                {t("homePage.button")}
              </Typography>
            </Button>
          )}
        </Box>
        <Box
          sx={{
            mt: { xs: 2, md: 0 },
            height: "auto",
            width: { xs: "300px", md: "600px", lg: "800px" },
            position: { xs: "static", md: "absolute" },
            bottom: 0,
            right: { xs: "50%", md: -100 },
            transform: { xs: "translateX(0)", md: "translateX(0)" },
            top: { xs: "50%", md: 0 },
          }}
        >
          <img
            src={homeImg}
            alt="home Image"
            style={{
              width: "100%",
              height: "100%",
              objectFit: "contain",
            }}
          />
        </Box>
      </Box>
    </MainLayout>
  );
};

export default HomePage;

import MailOutlineIcon from "@mui/icons-material/MailOutline";
import { useTranslation } from "react-i18next";
import { useSnackbar } from "notistack";
import { Box, Grid, Link, Typography } from "@mui/material";
import verifyPose from "../assets/verifyPose.svg";
import { postResendVerificationToken } from "../api/accountApi";
import { useLocation } from "react-router-dom";

const WaitForVerifyPage = () => {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const { state } = useLocation();

  const handleResendMail = () => {
    const accountId = state ? state.id : "-1";
    postResendVerificationToken(accountId).then((response) => {
      if (response.status === 200) {
        enqueueSnackbar(t("waitForVerifyPage.toastSuccess"), {
          variant: "success",
        });
      } else {
        enqueueSnackbar(t("waitForVerifyPage.toastError"), {
          variant: "error",
        });
      }
    });
  };

  return (
    //<MainLayout hideMenuEntries>
    <Grid
      sx={{
        display: "flex",
        flexDirection: "column",
        justifyContent: { xs: "flex-start", md: "center" },
        alignItems: "center",
        height: "100vh",
        maxHeight: "100vh",
        overflow: "hidden",
        position: "relative",
      }}
      container
    >
      <Grid
        sx={{
          mt: { xs: 10, md: 4 },
          display: "flex",
          flexDirection: "column",
          justifyContent: { xs: "flex-start", md: "center" },
          alignItems: "center",
          textAlign: "center",
        }}
        item
        xs={9}
        md={4}
      >
        <Box
          sx={{
            width: { xs: "80px", md: "96px" },
            height: "auto",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <MailOutlineIcon sx={{ fontSize: { xs: "80px", md: "96px" } }} />
        </Box>
        <Typography
          variant="h2"
          sx={{
            mt: 4,
            fontSize: { xs: "32px", md: "40px" },
            fontWeight: "700",
          }}
        >
          {t("waitForVerifyPage.header")}
        </Typography>
        <Typography
          sx={{
            mt: 2,
            color: "text.secondary",
            mb: { xs: 10, md: 0 },
            fontSize: { xs: "16px", md: "20px" },
          }}
        >
          {t("waitForVerifyPage.description")}
        </Typography>
        <Typography
          sx={{
            mt: 2,
            color: "text.secondary",
            mb: { xs: 10, md: 0 },
            fontSize: { xs: "12px", md: "16px" },
          }}
        >
          <Link sx={{ cursor: "pointer" }} onClick={handleResendMail}>
            {t("waitForVerifyPage.clickHere")}
          </Link>
          {t("waitForVerifyPage.resendMailDescription")}
        </Typography>
        <Box
          sx={{
            width: { xs: "500px", md: "600px" },
            height: { xs: "500px", md: "800px" },
            position: "absolute",
            bottom: 0,
            left: { xs: "50%", md: -200 },
            transform: { xs: "translateX(-50%)", md: "translateX(0)" },
            top: { xs: "50%", md: 200 },
          }}
        >
          <img
            src={verifyPose}
            alt="verifyPose"
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
        </Box>
      </Grid>
    </Grid>
    // </MainLayout>
  );
};

export default WaitForVerifyPage;

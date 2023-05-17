import HelpIcon from "@mui/icons-material/Help";
import { useTranslation } from "react-i18next";
import { Box, Grid, Typography } from "@mui/material";
import verifyPose from "../assets/verifyPose.svg";

const NotFound = () => {
  const { t } = useTranslation();

  return (
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
          <HelpIcon sx={{ fontSize: { xs: "80px", md: "96px" } }} />
        </Box>
        <Typography
          variant="h2"
          sx={{
            mt: 4,
            fontSize: { xs: "32px", md: "40px" },
            fontWeight: "700",
          }}
        >
          404 Not Found
        </Typography>
        <Typography
          sx={{
            mt: 2,
            color: "text.secondary",
            mb: { xs: 10, md: 0 },
            fontSize: { xs: "16px", md: "20px" },
          }}
        >
          The page you are looking for does not exist.
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
  );
};

export default NotFound;

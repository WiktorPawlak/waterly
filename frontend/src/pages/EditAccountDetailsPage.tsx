import { MainLayout } from "../layouts/MainLayout";
import { Box, Button, Divider, Input, Typography } from "@mui/material";
import loginPose from "../assets/loginPose.svg";
import { useTranslation } from "react-i18next";

const EditAccountDetailsPage = () => {
  const { t } = useTranslation();
  return (
    <MainLayout>
      <Box
        sx={{
          position: "relative",
          height: "100vh",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography
          variant="h4"
          sx={{ fontWeight: "700", mb: { xs: 10, md: 10 } }}
        >
          {t("editAccountDetailsPage.header")}
        </Typography>

        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            width: { xs: "100%", md: "50%" },
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              width: "100%",
            }}
          >
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
              }}
            >
              <Typography
                variant="h4"
                sx={{ fontSize: "16px", fontWeight: "700" }}
              >
                {t("editAccountDetailsPage.editAccountDetailEntry.emailLabel")}
              </Typography>
              <Button
                variant="contained"
                sx={{ textTransform: "none", fontWeight: "700" }}
              >
                {t("editAccountDetailsPage.button")}
              </Button>
            </Box>
            <Input
              name="name"
              disableUnderline={true}
              value="jan.kowalski@gmail.com"
              sx={{ color: "text.secondary" }}
            />
          </Box>

          <Divider variant="middle" sx={{ my: 2 }} />

          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              width: "100%",
            }}
          >
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
              }}
            >
              <Typography
                variant="h4"
                sx={{ fontSize: "16px", fontWeight: "700" }}
              >
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.passwordLabel"
                )}
              </Typography>
              <Button
                variant="contained"
                sx={{ textTransform: "none", fontWeight: "700" }}
              >
                {t("editAccountDetailsPage.button")}
              </Button>
            </Box>
            <Input
              name="name"
              disableUnderline={true}
              value="**********"
              sx={{ color: "text.secondary" }}
            />
          </Box>

          <Divider variant="middle" sx={{ my: 2 }} />

          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              width: "100%",
            }}
          >
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
              }}
            >
              <Typography
                variant="h4"
                sx={{ fontSize: "16px", fontWeight: "700" }}
              >
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.phoneNumberLabel"
                )}
              </Typography>
              <Button
                variant="contained"
                sx={{ textTransform: "none", fontWeight: "700" }}
              >
                {t("editAccountDetailsPage.button")}
              </Button>
            </Box>
            <Input
              name="name"
              disableUnderline={true}
              value="333888444"
              sx={{ color: "text.secondary" }}
            />
          </Box>
        </Box>

        <Box
          sx={{
            width: { xs: "500px", md: "800px" },
            height: { xs: "500px", md: "800px" },
            position: "absolute",
            bottom: 0,
            right: { xs: "50%", md: -300 },
            transform: { xs: "translateX(-50%)", md: "translateX(0)" },
            top: { xs: "50%", md: 50 },
          }}
        >
          <img
            src={loginPose}
            alt="XD"
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
        </Box>
      </Box>
    </MainLayout>
  );
};

export default EditAccountDetailsPage;

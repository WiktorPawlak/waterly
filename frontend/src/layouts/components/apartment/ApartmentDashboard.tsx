import { useTranslation } from "react-i18next";
import { MainLayout } from "../../MainLayout";
import { Box } from "@mui/system";
import { Typography } from "@mui/material";

export const ApartmentsDashboard = () => {
  const { t } = useTranslation();

  return (
    <MainLayout>
      <Box
        sx={{
          height: "100vh",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("apartmentPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 10, md: 5 }, color: "text.secondary" }}>
          {t("apartmentPage.description")}
        </Typography>
      </Box>
    </MainLayout>
  );
};

import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { resolveApiError } from "../../api/apiErrors";
import { useSnackbar } from "notistack";
import { Box, Typography } from "@mui/material";
import { Loading } from "../../layouts/components/Loading";
import { ApartmentDto, getApartmentDetails } from "../../api/apartmentApi";
import { ApartmentDetails } from "../../layouts/components/apartment/ApartmentDetails";
import { MainLayout } from "../../layouts/MainLayout";

export const ApartmentDetailsPage = () => {
  const [apartmentDetails, setApartmentDetails] = useState<ApartmentDto | undefined>(
    undefined
  );

  const { enqueueSnackbar } = useSnackbar();
  const { t } = useTranslation();
  const { id } = useParams();

  const fetchApartmentDetails = async () => {
    const response = await getApartmentDetails(parseInt(id as string));
    if (response.status === 200) {
      setApartmentDetails(response.data);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  useEffect(() => {
    fetchApartmentDetails();
  }, []);

  if (!apartmentDetails) {
    return <Loading />;
  }

  return (
    <MainLayout>
      <Box
        sx={{
          position: "relative",
          display: "flex",
          flexDirection: "column",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("apartmentDetailsPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 5, md: 5 }, color: "text.secondary" }}>
          {t("apartmentDetailsPage.description")}
        </Typography>
      </Box>
      <ApartmentDetails
        apartment={apartmentDetails}
      />
    </MainLayout>
  );
};

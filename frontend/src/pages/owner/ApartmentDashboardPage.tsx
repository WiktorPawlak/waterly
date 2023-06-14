import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { resolveApiError } from "../../api/apiErrors";
import { useSnackbar } from "notistack";
import { Box, Typography } from "@mui/material";
import { Loading } from "../../layouts/components/Loading";
import { ApartmentDto, getOwnerApartments } from "../../api/apartmentApi";
import { MainLayout } from "../../layouts/MainLayout";
import { GetPagedListDto, List } from "../../api/accountApi";
import { ApartmentDetails } from "../../layouts/components/apartment/ApartmentDetails";
import { ApartmentCard } from "../../layouts/components/apartment/ApartmentCard";
import { WaterMeterCard } from "../../layouts/components/watermeter/WaterMeterCard";
import { WaterMeterDto, getApartmentWaterMeters } from "../../api/waterMeterApi";

export const ApartmentDashboardPage = () => {
  const [apartmentsList, setApartmentsList] = useState<
    List<ApartmentDto> | undefined
  >(undefined);
  const [waterMeters, setWaterMeters] = useState<WaterMeterDto[]>();

  const { enqueueSnackbar } = useSnackbar();
  const { t } = useTranslation();
  const { id } = useParams();

  const getPagedListDto: GetPagedListDto = {
    page: 1,
    pageSize: 100,
    order: "asc",
    orderBy: "number",
  };

  const fetchWaterMeters = async () => {
    const response = await getApartmentWaterMeters(parseInt(id as string));
    if (response.status === 200) {
      setWaterMeters(response.data);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  const fetchOwnerApartments = async () => {
    const response = await getOwnerApartments(getPagedListDto);
    if (response.status === 200) {
      setApartmentsList(response.data);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  useEffect(() => {
    fetchOwnerApartments();
  }, []);

  useEffect(() => {
    if (id) {
      fetchWaterMeters();
    }
  }, [id]);

  if (!apartmentsList) {
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
          {t("apartmentsDashboardPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 5, md: 5 }, color: "text.secondary" }}>
          {t("apartmentsDashboardPage.description")}
        </Typography>
      </Box>
      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          height: "100%",
          justifyContent: "space-between",
        }}
      >
        <Box
          sx={{
            display: "flex",
            flexWrap: "wrap",
            flexDirection: id == null ? "row" : "column",
            justifyContent: "space-around",
            alignItems: "center",
            marginRight: "50px",
            height: "100%",
          }}
        >
          {apartmentsList.data.map((obj) => (
            <Box sx={{ marginBottom: "25px" }} key={obj.id}>
              <ApartmentCard apartment={obj} />
            </Box>
          ))}
        </Box>
        {id && (
          <ApartmentDetails
            apartment={
              apartmentsList.data.find(
                (obj) => obj.id === parseInt(id)
              ) as ApartmentDto
            }
          />
        )}
        {id && (
        <Box
          sx={{
            display: "flex",
            flexWrap: "wrap",
            height: "100%",
            justifyContent: "space-between",
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexWrap: "wrap",
              flexDirection: id == null ? "row" : "column",
              justifyContent: "space-around",
              alignItems: "center",
              marginRight: "50px",
              height: "100%",
            }}
          >
            {waterMeters?.map((obj) => (
              <Box sx={{ margin: "25px" }} key={obj.id}>
                <WaterMeterCard
                  handleEditButtonClick={() => {}}
                  handleReplaceButtonClick={() => {}}
                  waterMeter={{
                    id: obj.id,
                    active: obj.active,
                    expiryDate: obj.expiryDate,
                    expectedDailyUsage: obj.expectedDailyUsage || 0,
                    startingValue: obj.startingValue,
                    type: obj.type,
                    apartmentId: obj.apartmentId,
                    version: obj.version,
                  }}
                />
              </Box>
            ))}
          </Box>
        </Box>
        )}
      </Box>
    </MainLayout>
  );
};
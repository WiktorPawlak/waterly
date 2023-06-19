import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { resolveApiError } from "../../api/apiErrors";
import { useSnackbar } from "notistack";
import { Box, Button, Typography } from "@mui/material";
import { Loading } from "../../layouts/components/Loading";
import {
  ApartmentDto,
  getAllAprtmentsList,
  getApartmentDetails,
} from "../../api/apartmentApi";
import { ApartmentDetails } from "../../layouts/components/apartment/ApartmentDetails";
import { MainLayout } from "../../layouts/MainLayout";
import { AssignWaterMeterToApartmentDialog } from "../../layouts/components/watermeter/AssingWaterMeterToApartmentModal";
import AddIcon from "@mui/icons-material/Add";
import {
  WaterMeterDto,
  getApartmentWaterMeters,
  getWaterMeterById,
} from "../../api/waterMeterApi";
import { WaterMeterCard } from "../../layouts/components/watermeter/WaterMeterCard";
import { EditWaterMeterModal } from "../../layouts/components/watermeter/EditWaterMeterModal";
import { ReplaceWaterMeterModal } from "../../layouts/components/watermeter/ReplaceWaterMeterModal";
import { roles } from "../../types";
import { HttpStatusCode } from "axios";
import { GetPagedListDto, PaginatedList } from "../../api/accountApi";
import { useAccount } from "../../hooks/useAccount";
import AssessmentIcon from "@mui/icons-material/Assessment";
import { WaterMeterListDialog } from "../../layouts/components/watermeter/WaterMeterListDialog";

export const ApartmentDetailsPage = () => {
  const [apartmentDetails, setApartmentDetails] = useState<
    ApartmentDto | undefined
  >(undefined);
  const { account } = useAccount();
  const [waterMeters, setWaterMeters] = useState<WaterMeterDto[]>();

  const [editWaterMeterDialogOpen, setEditWaterMeterDialogOpen] =
    useState(false);
  const [replaceWaterMeterDialogOpen, setReplaceWaterMeterDialogOpen] =
    useState(false);
  const [selectedWaterMeter, setSelectedWaterMeter] = useState<
    WaterMeterDto | undefined
  >();
  const [selectedWaterMeterEtag, setSelectedWaterMeterEtag] = useState("");

  const { enqueueSnackbar } = useSnackbar();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { id } = useParams();
  const [assignWaterMeterDialogOpen, setAssignWaterMeterDialogOpen] =
    useState(false);

  const [apartmentsPageState, setApartmentsPageState] = useState<
    PaginatedList<ApartmentDto>
  >({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [apartmentsListRequest, setApartmentsListRequest] =
    useState<GetPagedListDto>({
      page: 1,
      pageSize: 100,
      order: "asc",
      orderBy: "number",
    });

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

  const fetchAllApartments = () => {
    getAllAprtmentsList(apartmentsListRequest, "").then((response) => {
      if (response.status === HttpStatusCode.Ok) {
        setApartmentsPageState(response.data!);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
    });
  };

  const handleEditButtonClick = async (id: number) => {
    if (account?.currentRole === roles.facilityManager) {
      const response = await getWaterMeterById(id);
      if (response.data) {
        setSelectedWaterMeter(response.data!);
        setSelectedWaterMeterEtag(response.headers!["etag"] as string);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
      setEditWaterMeterDialogOpen(true);
    }
  };

  const handleReplaceButtonClick = async (id: number) => {
    if (account?.currentRole === roles.facilityManager) {
      const response = await getWaterMeterById(id);
      if (response.data) {
        setSelectedWaterMeter(response.data!);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
      setReplaceWaterMeterDialogOpen(true);
    }
  };

  useEffect(() => {
    if (account?.currentRole === roles.facilityManager) {
      fetchAllApartments();
    }
    fetchApartmentDetails();
    fetchWaterMeters();
  }, [
    assignWaterMeterDialogOpen,
    editWaterMeterDialogOpen,
    replaceWaterMeterDialogOpen,
  ]);

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
        <EditWaterMeterModal
          isOpen={editWaterMeterDialogOpen}
          setIsOpen={setEditWaterMeterDialogOpen}
          waterMeter={selectedWaterMeter}
          apartments={apartmentsPageState}
          etag={selectedWaterMeterEtag}
        />
        <ReplaceWaterMeterModal
          isOpen={replaceWaterMeterDialogOpen}
          setIsOpen={setReplaceWaterMeterDialogOpen}
          waterMeter={selectedWaterMeter}
        />
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("apartmentDetailsPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 5, md: 5 }, color: "text.secondary" }}>
          {t("apartmentDetailsPage.description")}
        </Typography>
        <Box sx={{ display: "flex", rowGap: "20px" }}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            sx={{
              textTransform: "none",
              mb: { xs: 3, md: 2 },
              width: "30vh",
            }}
            onClick={() => setAssignWaterMeterDialogOpen(true)}
          >
            {t("assignWaterMeterDialog.addWaterMeter")}
          </Button>
          <WaterMeterListDialog apartmentId={parseInt(id!!)} />
          <Button
            variant="contained"
            startIcon={<AssessmentIcon />}
            sx={{
              textTransform: "none",
              mb: { xs: 3, md: 2, marginLeft: "20px" },
              width: "30vh",
            }}
            onClick={() => navigate(`/apartments/${id}/bills`)}
          >
            {t("apartmentDetailsPage.showBills")}
          </Button>
        </Box>
        <AssignWaterMeterToApartmentDialog
          isOpen={assignWaterMeterDialogOpen}
          setIsOpen={setAssignWaterMeterDialogOpen}
          apartmentId={apartmentDetails.id}
        />
        {/* <WaterMeterList apartmentId={apartmentDetails.id} /> */}
      </Box>
      <ApartmentDetails apartment={apartmentDetails} />

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
            flexDirection: "row",
            justifyContent: "space-around",
            alignItems: "center",
            marginRight: "50px",
            height: "100%",
          }}
        >
          {waterMeters?.map((obj) => (
            <Box sx={{ margin: "25px" }} key={obj.id}>
              <WaterMeterCard
                handleEditButtonClick={handleEditButtonClick}
                handleReplaceButtonClick={handleReplaceButtonClick}
                waterMeter={{
                  id: obj.id,
                  serialNumber: obj.serialNumber,
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
    </MainLayout>
  );
};

import { useState, useEffect } from "react";
import { Button, Dialog, DialogTitle, DialogContent } from "@mui/material";
import {
  WaterMeterChecksDto,
  WaterMeterDto,
  getWaterMatersByApartmentId,
  performWaterMeterChecks,
} from "../../../api/waterMeterApi";
import { WaterMeterCheckModal } from "./WaterMeterCheckModal";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../../api/apiErrors";
import { useTranslation } from "react-i18next";
import { useAccount } from "../../../hooks/useAccount";
import { roles } from "../../../types/rolesEnum";

interface Props {
  apartmentId: number;
}

export const WaterMeterListDialog = ({ apartmentId }: Props) => {
  const { t } = useTranslation();
  const [waterMeters, setWaterMeters] = useState<WaterMeterDto[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const { account } = useAccount();

  const managerAuthored = account?.currentRole === roles.facilityManager;

  useEffect(() => {
    fetchWaterMeters();
  }, []);

  const fetchWaterMeters = async () => {
    const response = await getWaterMatersByApartmentId(apartmentId);
    setWaterMeters(response.data ?? []);
  };

  const handleDialogOpen = () => {
    fetchWaterMeters();
    setIsDialogOpen(true);
  };

  const handleDialogClose = () => {
    setIsDialogOpen(false);
  };

  const handleFormSubmit = async (data: WaterMeterChecksDto) => {
    data.managerAuthored = managerAuthored;
    const response = await performWaterMeterChecks(data);

    if (response.status === 204) {
      enqueueSnackbar(t("WaterMeterListDialog.toast.created"), {
        variant: "success",
      });
      handleDialogClose();
    } else {
      if (response.error === "ERROR.INACTIVE_WATER_METER") {
        enqueueSnackbar(t("validation.waterMeterCheckOnInactiveWaterMeter"), {
          variant: "error",
        });
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
    }

    setIsDialogOpen(false);
  };

  return (
    <>
      <Button
        variant="contained"
        disabled={waterMeters.length === 0}
        sx={{
          textTransform: "none",
          mb: { xs: 3, md: 2 },
          ml: 2.5,
          width: "30vh",
        }}
        onClick={handleDialogOpen}
      >
        {t("WaterMeterListDialog.button")}
      </Button>
      <Dialog open={isDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>{t("WaterMeterListDialog.header")}</DialogTitle>
        <DialogContent>
          <WaterMeterCheckModal
            waterMeters={waterMeters}
            onSubmit={handleFormSubmit}
          />
        </DialogContent>
      </Dialog>
    </>
  );
};

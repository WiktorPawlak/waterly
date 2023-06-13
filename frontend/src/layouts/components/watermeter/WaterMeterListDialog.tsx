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

interface Props {
  apartmentId: number;
}

export const WaterMeterListDialog = ({ apartmentId }: Props) => {
  const { t } = useTranslation();
  const [waterMeters, setWaterMeters] = useState<WaterMeterDto[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  useEffect(() => {
    const fetchWaterMeters = async () => {
      const response = await getWaterMatersByApartmentId(apartmentId);
      setWaterMeters(response.data ?? []);
    };
    fetchWaterMeters();
  }, [apartmentId]);

  const handleDialogOpen = () => {
    setIsDialogOpen(true);
  };

  const handleDialogClose = () => {
    setIsDialogOpen(false);
  };

  const handleFormSubmit = async (data: WaterMeterChecksDto) => {
    const response = await performWaterMeterChecks(data);

    if (response.status === 204) {
      enqueueSnackbar(t("WaterMeterListDialog.toast.created"), {
        variant: "success",
      });
      handleDialogClose();
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }

    setIsDialogOpen(false);
  };

  return (
    <>
      <Button
        variant="contained"
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

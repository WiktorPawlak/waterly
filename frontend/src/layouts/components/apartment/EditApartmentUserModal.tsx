import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Tooltip,
  tooltipClasses,
  TooltipProps,
  Typography,
} from "@mui/material";
import { Box, Stack, styled } from "@mui/system";
import { useTranslation } from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";
import { enqueueSnackbar } from "notistack";
import ErrorIcon from "@mui/icons-material/Error";
import {
  ApartmentDto,
  changeApartmentOwner,
  ChangeApartmentOwnerDto,
  WaterMeterExpectedUsagesDto,
} from "../../../api/apartmentApi";
import { useEffect, useState } from "react";
import { OwnerAccountsSelect } from "../account/OwnersAccountsSelect";
import { resolveApiError } from "../../../api/apiErrors";
import {
  getApartmentWaterMeters,
  WaterMeterDto,
} from "../../../api/waterMeterApi";
import WaterDropIcon from "@mui/icons-material/WaterDrop";
import GasMeterIcon from "@mui/icons-material/GasMeter";

const ErrorTooltip = styled(({ className, ...props }: TooltipProps) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(({ theme }) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: "#ff3333",
    color: "white",
    fontSize: 15,
    padding: "10px",
  },
}));

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  apartment?: ApartmentDto;
  etag: string;
}

interface Error {
  error: string;
  index: number;
}

export const EditApartmentUserModal = ({
  isOpen,
  setIsOpen,
  apartment,
  etag,
}: Props) => {
  const { t } = useTranslation();
  const [ownerId, setOwnerId] = useState<number | undefined>();
  const [isLoading, setIsLoading] = useState(false);
  const [waterMeters, setWaterMeters] = useState<WaterMeterDto[]>([]);
  const [waterMeterExpectedUsages, setWaterMeterExpectedUsages] = useState<
    WaterMeterExpectedUsagesDto[]
  >([]);
  const [apartmentId, setApartmentId] = useState<number | undefined>(
    apartment?.id
  );
  const [errors, setErrors] = useState<Error[]>([]);

  const handleClose = () => {
    setIsOpen(false);
    setTimeout(() => setOwnerId(undefined), 1000);
  };

  useEffect(() => {
    fetchData();
  }, [apartmentId]);

  useEffect(() => {
    setWaterMeterExpectedUsages(
      waterMeters.map((meter) => ({
        waterMeterId: meter.id,
        expectedMonthlyUsage: 0,
      }))
    );
  }, [waterMeters]);

  const updateExpectedUsage = (value: number, id: number) => {
    setWaterMeterExpectedUsages((meters) => {
      meters.find((meter) => meter.waterMeterId === id)!!.expectedMonthlyUsage =
        value;
      return meters;
    });
  };

  useEffect(() => {
    setWaterMeters(waterMeters);
  }, [waterMeters]);

  useEffect(() => {
    if (apartment?.id !== apartmentId) {
      setApartmentId(apartment?.id);
    }
  }, [apartment]);

  const fillExpectedUsage = (e: string, id: number, index: number) => {
    checkValidation(e, index);
    updateExpectedUsage(parseFloat(e), id);
  };

  const hasAnyErrors = () => {
    return errors.length > 0;
  };

  const [hasErrors, setHasErrors] = useState(hasAnyErrors());

  const checkValidation = (value: string, index: number) => {
    const regex = /^\d+(\.\d{3})?$/;

    if (!regex.test(value) || value.length < 1 || value.length > 11) {
      setErrors((prevErrors) => {
        const updatedErrors = prevErrors.filter(
          (error) => error.index !== index
        );
        updatedErrors.push({
          index,
          error: t("waterMeterOwnerChange.waterMeterError"),
        });
        return updatedErrors;
      });
    } else {
      setErrors((prevErrors) =>
        prevErrors.filter((error) => error.index !== index)
      );
    }
    setHasErrors(hasAnyErrors());
  };

  const fetchData = async () => {
    setIsLoading(true);
    if (apartmentId !== undefined && apartmentId !== null) {
      const response = await getApartmentWaterMeters(apartmentId);
      if (response.status !== 200) {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      } else {
        setWaterMeters(response.data as WaterMeterDto[]);
      }
      setIsLoading(true);
    }
    return;
  };

  const handleFormSubmit = async () => {
    if (!ownerId) return;

    const dto: ChangeApartmentOwnerDto = {
      newOwnerId: ownerId,
      waterMeterExpectedUsages: waterMeterExpectedUsages,
    };

    const response = await changeApartmentOwner(apartment!!.id, dto, etag);
    if (response.status === 200) {
      enqueueSnackbar(t("apartmentPage.ownerChangedSuccessfully"), {
        variant: "success",
      });
      handleClose();
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  return (
    <Box
      sx={{
        flexDirection: "column",
        display: "flex",
        width: "900px !important",
      }}
    >
      <Box
        sx={{
          mt: { xs: 40, md: 0 },
          display: "flex",
          flexDirection: "column",
          alignItems: { xs: "center", md: "flex-start" },
          justifyContent: { xs: "center", md: "flex-start" },
        }}
      ></Box>
      <Box>
        <Dialog open={isOpen} onClose={handleClose}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <DialogTitle id="role-modal-title">
              {t("changeApartmentOwnerModal.editOwner")}
            </DialogTitle>
            <Button sx={{ width: "30px" }} onClick={handleClose}>
              <CloseIcon />
            </Button>
          </Box>
          <Stack direction="row" spacing={0}>
            <DialogContent sx={{ width: "300px" }}>
              <Box sx={{ width: "100%" }}>
                <Box
                  sx={{
                    display: "flex",
                    flexDirection: { xs: "column", md: "row" },
                  }}
                ></Box>
                <OwnerAccountsSelect
                  setOwnerId={setOwnerId}
                  ownerId={ownerId}
                  defaultOwnerId={apartment?.ownerId}
                />
              </Box>
            </DialogContent>
            <DialogContent>
              <Box sx={{ width: "100%" }}>
                <Box
                  sx={{
                    display: "flex",
                    flexDirection: { xs: "column", md: "row" },
                  }}
                ></Box>
                <Typography
                  variant="h4"
                  sx={{ fontSize: "16px", fontWeight: "700", mb: 2 }}
                >
                  {t("waterMeterOwnerChange.waterMeterHeader")}
                </Typography>
                {waterMeters.map((meter, index) => (
                  <Box
                    key={meter.id}
                    sx={{
                      border: "1px solid gray",
                      padding: "10px",
                      marginBottom: "10px",
                      boxShadow: 2,
                      borderRadius: "16px",
                    }}
                  >
                    <Typography key={meter.id}>
                      <GasMeterIcon color="primary" />
                      {meter.type === "HOT_WATER" ? (
                        <WaterDropIcon color="error" />
                      ) : meter.type === "COLD_WATER" ? (
                        <WaterDropIcon color="primary" />
                      ) : null}
                      <br />
                      {t("waterMeterOwnerChange.waterMeterId")}
                      {meter.id}
                      <br />
                      <br />
                      <TextField
                        error={errors.some((error) => error.index === index)}
                        helperText={
                          errors.find((error) => error.index === index)
                            ?.error ?? ""
                        }
                        label={t(
                          "waterMeterOwnerChange.waterMeterExpectedUsage"
                        )}
                        onChange={(e) => {
                          fillExpectedUsage(e.target.value, meter.id, index);
                        }}
                      />
                    </Typography>
                  </Box>
                ))}
              </Box>
            </DialogContent>
          </Stack>
          <DialogActions>
            <Button onClick={handleClose}>{t("common.close")}</Button>
            <ErrorTooltip
              placement="top"
              title={
                !ownerId ? (
                  <>
                    <ErrorIcon /> {t("apartmentPage.validation.selectedOwner")}
                  </>
                ) : (
                  ""
                )
              }
            >
              <span>
                <Button
                  disabled={!ownerId || hasAnyErrors()}
                  variant="contained"
                  color="primary"
                  onClick={handleFormSubmit}
                >
                  {t("common.confirm")}
                </Button>
              </span>
            </ErrorTooltip>
          </DialogActions>
        </Dialog>
      </Box>
    </Box>
  );
};

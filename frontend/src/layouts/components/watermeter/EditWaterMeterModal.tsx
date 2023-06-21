import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import { Box } from "@mui/system";
import { Trans, useTranslation } from "react-i18next";
import { resolveApiError } from "../../../api/apiErrors";
import CloseIcon from "@mui/icons-material/Close";
import { StyledTextField } from "../../../pages/admin/AccountDetailsPage/AccountDetailsPage.styled";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { plPL } from "@mui/x-date-pickers/locales";
import dayjs, { Dayjs } from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  EditWaterMeterSchema,
  editWaterMeterSchema,
} from "../../../validation/validationSchemas";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { updateWaterMeter, WaterMeterDto } from "../../../api/waterMeterApi";
import { ApartmentDropdown } from "../apartment/ApartmentDropdown";
import { PaginatedList } from "../../../api/accountApi";
import { ApartmentDto } from "../../../api/apartmentApi";

interface Props {
  waterMeter: WaterMeterDto | undefined;
  apartments: PaginatedList<ApartmentDto>;
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  etag: string;
}

export const EditWaterMeterModal = ({
  waterMeter,
  apartments,
  isOpen,
  setIsOpen,
  etag,
}: Props) => {
  const { t } = useTranslation();
  const [expiryDate, setExpiryDate] = useState<Dayjs | null>(
    dayjs(waterMeter?.expiryDate)
  );
  const [apartmentId, setApartmentId] = useState<number | null>(
    waterMeter?.apartmentId!
  );
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
  } = useForm<EditWaterMeterSchema>({
    resolver: zodResolver(editWaterMeterSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      serialNumber: "",
      expectedMonthlyUsage: "",
      startingValue: "",
    },
  });

  const {
    serialNumber: serialNumberError,
    expectedMonthlyUsage: expectedMonthlyUsageError,
    startingValue: startingValueError,
  } = errors;
  const serialNumberErrorMessage = serialNumberError?.message;
  const expectedMonthlyUsageErrorMessage = expectedMonthlyUsageError?.message;
  const startingValueErrorMessage = startingValueError?.message;

  const handleClose = () => {
    setIsOpen(false);
    reset();
  };

  useEffect(() => {
    if (waterMeter) {
      setExpiryDate(dayjs(waterMeter!.expiryDate));
      setValue("expectedMonthlyUsage", "");
      setValue("serialNumber", waterMeter.serialNumber.toString());
      setValue("startingValue", waterMeter!.startingValue.toString());
      setApartmentId(waterMeter!.apartmentId);
    }
  }, [waterMeter]);

  const handleConfirmAction = async (editWaterMeter: any) => {
    const response = await updateWaterMeter(
      waterMeter!.id,
      {
        ...editWaterMeter,
        expiryDate: expiryDate?.format("YYYY-MM-DD"),
        version: waterMeter?.version,
        id: waterMeter?.id,
        apartmentId: apartmentId!,
      },
      etag
    );

    if (response.status === 204) {
      enqueueSnackbar(t("editWaterMeterDialog.waterMeterEditedSuccessfully"), {
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
    <Box>
      <Dialog open={isOpen} onClose={handleClose}>
        <form onSubmit={handleSubmit(handleConfirmAction)}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <DialogTitle id="role-modal-title">
              {t("editWaterMeterDialog.editWaterMeter")}
            </DialogTitle>
            <Button sx={{ width: "30px" }} onClick={handleClose}>
              <CloseIcon />
            </Button>
          </Box>
          <DialogContent sx={{ width: "300px" }}>
            <Box
              sx={{ width: "100%", display: "flex", flexDirection: "column" }}
            >
              <StyledTextField
                variant="standard"
                sx={{ width: "100% !important" }}
                label={
                  <Trans
                    i18nKey={"editWaterMeterDialog.serialNumber"}
                    components={{ sup: <sup /> }}
                  />
                }
                {...register("serialNumber")}
                error={!!serialNumberError}
                helperText={
                  serialNumberErrorMessage && t(serialNumberErrorMessage)
                }
              />
              {waterMeter?.type !== "MAIN" && (
                <StyledTextField
                  autoFocus
                  label={
                    <Trans
                      i18nKey={"editWaterMeterDialog.expectedMonthlyUsage"}
                      components={{ sup: <sup /> }}
                    />
                  }
                  variant="standard"
                  sx={{ width: "100% !important" }}
                  {...register("expectedMonthlyUsage")}
                  error={!!expectedMonthlyUsageError}
                  helperText={
                    expectedMonthlyUsageErrorMessage &&
                    t(expectedMonthlyUsageErrorMessage)
                  }
                />
              )}
              <StyledTextField
                variant="standard"
                sx={{ width: "100% !important" }}
                label={
                  <Trans
                    i18nKey={"editWaterMeterDialog.startingValue"}
                    components={{ sup: <sup /> }}
                  />
                }
                {...register("startingValue")}
                error={!!startingValueError}
                helperText={
                  startingValueErrorMessage && t(startingValueErrorMessage)
                }
              />
              <LocalizationProvider
                adapterLocale="pl"
                dateAdapter={AdapterDayjs}
                localeText={
                  plPL.components.MuiLocalizationProvider.defaultProps
                    .localeText
                }
              >
                <DatePicker
                  label={t("editWaterMeterDialog.expiryDate")}
                  format="YYYY-MM-DD"
                  value={expiryDate}
                  onChange={(newValue) => setExpiryDate(newValue)}
                  views={["day", "month", "year"]}
                  sx={{ mb: 2 }}
                />
              </LocalizationProvider>
              {waterMeter?.type !== "MAIN" && (
                <ApartmentDropdown
                  apartments={apartments}
                  setApartmentId={setApartmentId}
                  apartmentId={waterMeter?.apartmentId as number}
                />
              )}
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>{t("common.close")}</Button>
            <Button variant="contained" color="primary" type="submit">
              {t("common.confirm")}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

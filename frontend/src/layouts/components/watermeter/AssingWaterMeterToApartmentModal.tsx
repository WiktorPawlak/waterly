import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  MenuItem,
  Select,
} from "@mui/material";
import { Box } from "@mui/system";
import { Trans, useTranslation } from "react-i18next";
import { resolveApiError } from "../../../api/apiErrors";
import CloseIcon from "@mui/icons-material/Close";
import { StyledTextField } from "../../../pages/admin/AccountDetailsPage/AccountDetailsPage.styled";
import { useState } from "react";
import { enqueueSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { plPL } from "@mui/x-date-pickers/locales";
import dayjs, { Dayjs } from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  assignWaterMeterToApartmentSchema,
  AssignWaterMeterToApartmentSchema,
} from "../../../validation/validationSchemas";
import { assignWaterMeterToApartment } from "../../../api/apartmentApi";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  apartmentId: number;
}

export const AssignWaterMeterToApartmentDialog = ({
  isOpen,
  setIsOpen,
  apartmentId,
}: Props) => {
  const { t } = useTranslation();
  const [expiryDate, setExpiryDate] = useState<Dayjs | null>(dayjs());

  const handleReset = () => {
    reset();
  };

  const handleClose = () => {
    setIsOpen(false);
    handleReset();
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset
  } = useForm<AssignWaterMeterToApartmentSchema>({
      resolver: zodResolver(assignWaterMeterToApartmentSchema),
      mode: "onChange",
      reValidateMode: "onChange",
      defaultValues: {
          serialNumber: "",
          startingValue: ""
      },
  });

  const {
      serialNumber: serialNumberError,
      startingValue: startingValueError
  } = errors;
  const serialNumberErrorMessage = serialNumberError?.message;
  const startingValueErrorMessage = startingValueError?.message;

  const handleFormSubmit = async (formData: any) => {
    const response = await assignWaterMeterToApartment(apartmentId, {
      ...formData,
      expiryDate: expiryDate?.format("YYYY-MM-DD"),
    });
    if (response.status === 201) {
      enqueueSnackbar(
        t("assignWaterMeterDialog.waterMeterAssignedSuccessfully"),
        {
          variant: "success",
        }
      );
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
        <form onSubmit={handleSubmit(handleFormSubmit)}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <DialogTitle id="role-modal-title">
              {t("assignWaterMeterDialog.addWaterMeter")}
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
                label={<Trans i18nKey={"assignWaterMeterDialog.serialNumber"} components={{ sup: <sup /> }} />}
                {...register("serialNumber")}
                error={!!serialNumberError}
                helperText={serialNumberErrorMessage && t(serialNumberErrorMessage)}
              />
              <StyledTextField
                autoFocus
                label={
                  <Trans
                    i18nKey={"assignWaterMeterDialog.startingValue"}
                    components={{ sup: <sup /> }}
                  />
                }
                variant="standard"
                sx={{ width: "100% !important" }}
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
                  label={t("assignWaterMeterDialog.date")}
                  format="YYYY-MM-DD"
                  value={expiryDate}
                  onChange={(newValue) => setExpiryDate(newValue)}
                  views={["month", "year", "day"]}
                  sx={{ mb: 2 }}
                />
              </LocalizationProvider>
              <FormControl>
                {t("assignWaterMeterDialog.type")}
                <Select
                  autoFocus
                  label={
                    <Trans
                      i18nKey={"assignWaterMeterDialog.type"}
                      components={{ sup: <sup /> }}
                    />
                  }
                  variant="standard"
                  sx={{ width: "100% !important" }}
                  {...register("type")}
                >
                  <MenuItem value="COLD_WATER">
                    {t("assignWaterMeterDialog.coldWater")}
                  </MenuItem>
                  <MenuItem value="HOT_WATER">
                    {t("assignWaterMeterDialog.hotWater")}
                  </MenuItem>
                </Select>
              </FormControl>
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

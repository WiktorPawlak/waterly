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
import { useState } from "react";
import { enqueueSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { plPL } from "@mui/x-date-pickers/locales";
import dayjs, { Dayjs } from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  addInvoiceSchema,
  AddInvoiceSchema,
} from "../../../validation/validationSchemas";
import { addInvoice } from "../../../api/invoiceApi";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const AddInvoiceDialog = ({ isOpen, setIsOpen }: Props) => {
  const { t } = useTranslation();
  const [date, setDate] = useState<Dayjs | null>(dayjs());

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
    reset,
  } = useForm<AddInvoiceSchema>({
    resolver: zodResolver(addInvoiceSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      invoiceNumber: "",
      waterUsage: "",
    },
  });

  const {
    invoiceNumber: invoiceNumberError,
    waterUsage: waterUsageError,
  } = errors;
  const invoiceNumberErrorMessage = invoiceNumberError?.message;
  const waterUsageErrorMessage = waterUsageError?.message;

  const handleFormSubmit = async (formData: any) => {
    const response = await addInvoice({
      ...formData,
      date: date?.format("YYYY-MM"),
    });
    if (response.status === 201) {
      enqueueSnackbar(t("addInvoiceModal.invoiceAddedSuccessfully"), {
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
        <form onSubmit={handleSubmit(handleFormSubmit)}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <DialogTitle id="role-modal-title">
              {t("addInvoiceModal.addInvoice")}
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
                autoFocus
                label={
                  <Trans
                    i18nKey={"addInvoiceModal.invoiceNumber"}
                    components={{ sup: <sup /> }}
                  />
                }
                variant="standard"
                sx={{ width: "100% !important" }}
                {...register("invoiceNumber")}
                error={!!invoiceNumberErrorMessage}
                helperText={
                  invoiceNumberErrorMessage && t(invoiceNumberErrorMessage)
                }
              />
              <StyledTextField
                variant="standard"
                sx={{ width: "100% !important" }}
                label={
                  <Trans
                    i18nKey={"addInvoiceModal.waterUsage"}
                    components={{ sup: <sup /> }}
                  />
                }
                {...register("waterUsage")}
                error={!!waterUsageErrorMessage}
                helperText={waterUsageErrorMessage && t(waterUsageErrorMessage)}
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
                  label={t("addInvoiceModal.date")}
                  format="YYYY-MM"
                  value={date}
                  onChange={(newValue) => setDate(newValue)}
                  views={["month", "year"]}
                  sx={{ mb: 2 }}
                />
              </LocalizationProvider>
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

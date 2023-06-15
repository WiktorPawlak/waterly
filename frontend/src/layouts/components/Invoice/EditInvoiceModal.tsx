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
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { InvoiceDto, updateInvoice } from "../../../api/invoiceApi";
import {
  EditInvoiceSchema,
  editInvoiceSchema,
} from "../../../validation/validationSchemas";

interface Props {
  invoice: InvoiceDto | undefined;
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  etag: string;
}

export const EditInvoiceModal = ({
  invoice,
  isOpen,
  setIsOpen,
  etag,
}: Props) => {
  const { t } = useTranslation();
  const [date, setDate] = useState<Dayjs | null>(dayjs(invoice?.date));
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
  } = useForm<EditInvoiceSchema>({
    resolver: zodResolver(editInvoiceSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      totalCost: invoice?.totalCost.toString(),
      waterUsage: invoice?.waterUsage.toString(),
      invoiceNumber: invoice?.invoiceNumber,
    },
  });

  const {
    totalCost: totalCostError,
    waterUsage: waterUsageError,
    invoiceNumber: invoiceNumberError,
  } = errors;
  const totalCostErrorMessage = totalCostError?.message;
  const waterUsageErrorMessage = waterUsageError?.message;
  const invoiceNumberErrorMessage = invoiceNumberError?.message;

  const handleClose = () => {
    setIsOpen(false);
    reset();
  };

  useEffect(() => {
    if (invoice) {
      setValue("waterUsage", invoice!.waterUsage.toString());
      setValue("totalCost", invoice!.totalCost.toString());
      setValue("invoiceNumber", invoice!.invoiceNumber);
      setDate(dayjs(invoice!.date));
    }
  }, [invoice]);

  const handleConfirmAction = async (editInvoice: any) => {
    const response = await updateInvoice(
      invoice!.id,
      {
        ...editInvoice,
        date: date?.format("YYYY-MM-DD"),
        version: invoice?.version,
        id: invoice?.id,
      },
      etag
    );

    if (response.status === 200) {
      enqueueSnackbar(t("editInvoiceModal.invoiceEditedSuccessfully"), {
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
              {t("editInvoiceModal.editInvoice")}
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
                    i18nKey={"editInvoiceModal.invoiceNumber"}
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
                    i18nKey={"editInvoiceModal.waterUsage"}
                    components={{ sup: <sup /> }}
                  />
                }
                {...register("waterUsage")}
                error={!!waterUsageErrorMessage}
                helperText={waterUsageErrorMessage && t(waterUsageErrorMessage)}
              />
              <StyledTextField
                label={
                  <Trans
                    i18nKey={"editInvoiceModal.totalCost"}
                    components={{ sup: <sup /> }}
                  />
                }
                sx={{ width: "100% !important" }}
                variant="standard"
                {...register("totalCost")}
                error={!!totalCostErrorMessage}
                helperText={totalCostErrorMessage && t(totalCostErrorMessage)}
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
                  label={t("editInvoiceModal.date")}
                  format="YYYY-MM-DD"
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

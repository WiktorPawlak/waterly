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
import { plPL } from '@mui/x-date-pickers/locales';
import dayjs, { Dayjs } from 'dayjs';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { zodResolver } from "@hookform/resolvers/zod";
import {
    AddTariffSchema,
    addTariffSchema,
} from "../../../validation/validationSchemas";
import { addTariff } from "../../../api/tariffsApi";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";

interface Props {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
}

export const AddTariffDialog = ({
    isOpen,
    setIsOpen,
}: Props) => {
    const { t } = useTranslation();
    const [startDate, setStartDate] = useState<Dayjs | null>(dayjs());
    const [endDate, setEndDate] = useState<Dayjs | null>(dayjs());

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
    } = useForm<AddTariffSchema>({
        resolver: zodResolver(addTariffSchema),
        mode: "onChange",
        reValidateMode: "onChange",
        defaultValues: {
            coldWaterPrice: "",
            hotWaterPrice: "",
            trashPrice: ""
        },
    });

    const {
        coldWaterPrice: coldWaterPriceError,
        hotWaterPrice: hotWaterPriceError,
        trashPrice: trashPriceError
    } = errors;
    const coldWaterPriceErrorMessage = coldWaterPriceError?.message;
    const hotWaterPriceErrorMessage = hotWaterPriceError?.message;
    const trashPriceErrorMessage = trashPriceError?.message;

    const handleFormSubmit = async (formData: any) => {
        const response = await addTariff(
            {
                ...formData,
                startDate: startDate?.format('YYYY-MM-DD'),
                endDate: endDate?.format('YYYY-MM-DD')
            }
        );
        if (response.status === 201) {
            enqueueSnackbar(t("addTariffDialog.tariffAddedSuccessfully"), {
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
                            {t("addTariffDialog.addTariff")}
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
                                label={<Trans i18nKey={"editTariffDialog.coldWaterPrice"} components={{ sup: <sup /> }} />}
                                variant="standard"
                                sx={{ width: "100% !important" }}
                                {...register("coldWaterPrice")}
                                error={!!coldWaterPriceErrorMessage}
                                helperText={coldWaterPriceErrorMessage && t(coldWaterPriceErrorMessage)}
                            />
                            <StyledTextField
                                variant="standard"
                                sx={{ width: "100% !important" }}
                                label={<Trans i18nKey={"editTariffDialog.hotWaterPrice"} components={{ sup: <sup /> }} />}
                                {...register("hotWaterPrice")}
                                error={!!hotWaterPriceErrorMessage}
                                helperText={hotWaterPriceErrorMessage && t(hotWaterPriceErrorMessage)}
                            />
                            <StyledTextField
                                label={<Trans i18nKey={"editTariffDialog.trashPrice"} components={{ sup: <sup /> }} />}
                                sx={{ width: "100% !important" }}
                                variant="standard"
                                {...register("trashPrice")}
                                error={!!trashPriceErrorMessage}
                                helperText={trashPriceErrorMessage && t(trashPriceErrorMessage)}
                            />
                            <LocalizationProvider adapterLocale="pl" dateAdapter={AdapterDayjs} localeText={plPL.components.MuiLocalizationProvider.defaultProps.localeText}>
                                <DatePicker
                                    label={t("editTariffDialog.startDate")}
                                    format="YYYY-MM"
                                    maxDate={endDate}
                                    value={startDate}
                                    onChange={(newValue) => setStartDate(newValue)}
                                    views={['month', 'year']}
                                    sx={{ mb: 2 }}
                                />
                                <DatePicker
                                    label={t("editTariffDialog.endDate")}
                                    format="YYYY-MM"
                                    minDate={startDate}
                                    value={endDate}
                                    onChange={(newValue) => setEndDate(newValue)}
                                    views={['month', 'year']}
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

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
import { plPL } from '@mui/x-date-pickers/locales';
import dayjs, { Dayjs } from 'dayjs';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { zodResolver } from "@hookform/resolvers/zod";
import {
    EditTariffSchema,
    editTariffSchema,
} from "../../../validation/validationSchemas";
import { TariffDto, getTariffById, updateTariff } from "../../../api/tariffsApi";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";


interface Props {
    tariff: TariffDto | undefined;
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    etag: string;
}

export const EditTariffDialog = ({
    tariff,
    isOpen,
    setIsOpen,
    etag
}: Props) => {
    const { t } = useTranslation();
    const [startDate, setStartDate] = useState<Dayjs | null>(dayjs(tariff?.startDate));
    const [endDate, setEndDate] = useState<Dayjs | null>(dayjs(tariff?.endDate));
    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
        setValue
    } = useForm<EditTariffSchema>({
        resolver: zodResolver(editTariffSchema),
        mode: "onChange",
        reValidateMode: "onChange",
        defaultValues: {
            coldWaterPrice: tariff?.coldWaterPrice.toString(),
            hotWaterPrice: tariff?.hotWaterPrice.toString(),
            trashPrice: tariff?.trashPrice.toString()
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

    const handleClose = () => {
        console.log(tariff);
        setIsOpen(false);
        reset();
    };

    useEffect(() => {
        if (tariff) {
            setValue("hotWaterPrice", tariff!.hotWaterPrice.toString());
            setValue("coldWaterPrice", tariff!.coldWaterPrice.toString());
            setValue("trashPrice", tariff!.trashPrice.toString());
            setStartDate(dayjs(tariff!.startDate));
            setEndDate(dayjs(tariff!.endDate));
        }
    }, [tariff])


    const handleConfirmAction = async (editTariff: any) => {
        const response = await updateTariff(
            tariff!.id,
            {
                ...editTariff,
                startDate: startDate?.format('YYYY-MM-DD'),
                endDate: endDate?.format('YYYY-MM-DD'),
                version: tariff?.version,
                id: tariff?.id
            },
            etag
        );

        if (response.status === 200) {
            enqueueSnackbar(t("editTariffDialog.tariffEditedSuccesfully"), {
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
                            {t("editTariffDialog.editTariff")}
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

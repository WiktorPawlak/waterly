import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Tooltip, tooltipClasses, TooltipProps,} from "@mui/material";
import {Box, styled} from "@mui/system";
import {useTranslation} from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";
import {createApartmentSchema, CreateApartmentSchemaType,} from "../../../validation/validationSchemas";
import {zodResolver} from "@hookform/resolvers/zod";
import {useForm} from "react-hook-form";
import {resolveApiError} from "../../../api/apiErrors";
import {enqueueSnackbar} from "notistack";
import ErrorIcon from "@mui/icons-material/Error";
import {createAprtment} from "../../../api/apartmentApi";
import {useState} from "react";
import {OwnerAccountsSelect} from "../account/OwnersAccountsSelect";

const ErrorTooltip = styled(({className, ...props}: TooltipProps) => (
    <Tooltip {...props} classes={{popper: className}}/>
))(({theme}) => ({
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
}

export const CreateApartmentDialog = ({isOpen, setIsOpen}: Props) => {
    const {t} = useTranslation();
    const [ownerId, setOwnerId] = useState<number>();

    const {
        register,
        handleSubmit,
        formState: {errors},
        reset,
    } = useForm<CreateApartmentSchemaType>({
        resolver: zodResolver(createApartmentSchema),
        mode: "onChange",
        reValidateMode: "onChange",
        defaultValues: {
            number: "",
            area: "10",
        },
    });

    const handleClose = () => {
        setIsOpen(false);
        handleReset();
        setTimeout(() => setOwnerId(undefined), 1000);
    };

    const handleReset = () => {
        reset();
    };

    const {number: numberError, area: areaError} = errors;

    const numberErrorMessage = numberError?.message;
    const areaErrorMessage = areaError?.message;

    const handleFormSubmit = async (formData: CreateApartmentSchemaType) => {
        if (!ownerId) return;

        const response = await createAprtment({
            number: formData.number,
            area: parseFloat(formData.area),
            ownerId,
        });

        if (response.status === 201) {
            enqueueSnackbar(t("apartmentPage.apartmentCreatedSuccessfully"), {
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
                width: "60%",
            }}
        >
            <Box
                sx={{
                    mt: {xs: 40, md: 0},
                    display: "flex",
                    flexDirection: "column",
                    alignItems: {xs: "center", md: "flex-start"},
                    justifyContent: {xs: "center", md: "flex-start"},
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
                            {t("apartmentPage.createApartment")}
                        </DialogTitle>
                        <Button sx={{width: "30px"}} onClick={handleClose}>
                            <CloseIcon/>
                        </Button>
                    </Box>
                    <DialogContent sx={{width: "400px"}}>
                        <Box sx={{width: "100%"}}>
                            <Box
                                sx={{
                                    display: "flex",
                                    flexDirection: {xs: "column", md: "row"},
                                }}
                            >
                                <TextField
                                    label={t("apartmentPage.dataGrid.header.number")}
                                    {...register("number")}
                                    error={!!numberErrorMessage}
                                    helperText={numberErrorMessage && t(numberErrorMessage)}
                                    variant="standard"
                                    name="number"
                                    sx={{
                                        mb: 3,
                                        mr: {xs: 0, md: 5},
                                        "& label": {
                                            color: "text.secondary",
                                        },
                                        "& label.Mui-focused": {
                                            color: "primary.main",
                                        },
                                    }}
                                />
                                <TextField
                                    label={t("apartmentPage.dataGrid.header.area")}
                                    {...register("area")}
                                    error={!!areaErrorMessage}
                                    helperText={areaErrorMessage && t(areaErrorMessage)}
                                    InputProps={{
                                        inputProps: {step: 0.5, min: 1.0, max: 999.99},
                                    }}
                                    variant="standard"
                                    name="area"
                                    type="number"
                                    sx={{
                                        mb: 3,
                                        "& label": {
                                            color: "text.secondary",
                                        },
                                        "& label.Mui-focused": {
                                            color: "primary.main",
                                        },
                                    }}
                                />
                            </Box>
                            <OwnerAccountsSelect setOwnerId={setOwnerId} ownerId={ownerId}/>
                        </Box>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose}>{t("common.close")}</Button>

                        <ErrorTooltip
                            placement="top"
                            title={
                                !ownerId ? (
                                    <>
                                        <ErrorIcon/> {t("apartmentPage.validation.selectedOwner")}
                                    </>
                                ) : (
                                    ""
                                )
                            }
                        >
              <span>
                <Button
                    disabled={!ownerId}
                    variant="contained"
                    color="primary"
                    onClick={handleSubmit(handleFormSubmit)}
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

import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, } from "@mui/material";
import { Box } from "@mui/system";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { LoginRequestBody, LoginResponse, postLogin, } from "../../../../api/authApi";
import { useAccount } from "../../../../hooks/useAccount";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../../../api/apiErrors";
import { TwoFactorCodeSchema, twoFactorCodeSchema } from "../../../../validation/validationSchemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

interface Props {
    isOpen: boolean;
    login: string;
    password: string;
    setIsOpen: (isOpen: boolean) => void;
}

export const EnterTwoFACodeModal = ({
    isOpen,
    setIsOpen,
    login,
    password,
}: Props) => {
    const { addUserToStorage } = useAccount();
    const { t } = useTranslation();

    const handleClose = () => {
        setIsOpen(false);
    };

    const {
        register,
        handleSubmit,
        getValues,
        formState: { errors },
    } = useForm<TwoFactorCodeSchema>({
        resolver: zodResolver(twoFactorCodeSchema),
        mode: "onChange",
        reValidateMode: "onChange",
        defaultValues: {
            code: ""
        }
    });

    const twoFactorCodeError = errors?.code?.message;

    const handleConfirm = async () => {
        const loginDto: LoginRequestBody = {
            login: login,
            password: password,
            twoFACode: getValues().code,
        };
        const response: LoginResponse<string> | null = await postLogin(loginDto);

        if (response.status === 200) {
            const token = response!.data as string;
            addUserToStorage(token);
            enqueueSnackbar(t(t("loginPage.succesfulLogin")), {
                variant: "success",
            });
        } else {
            enqueueSnackbar(t(resolveApiError(response.error)), {
                variant: "error",
            });
        }
    };

    return (
        <Box
            sx={{
                display: "flex",
                alignItems: "flex-end",
                justifyContent: "flex-end",
            }}
        >
            <Dialog open={isOpen} onClose={handleClose}>
                <form onSubmit={handleSubmit(handleConfirm)}>
                    <DialogTitle>{t("twoFactor.header")}</DialogTitle>
                    <DialogContent>
                        <DialogContentText>{t("twoFactor.description")}</DialogContentText>
                        <TextField
                            {...register("code")}
                            error={!!twoFactorCodeError}
                            helperText={twoFactorCodeError && t(twoFactorCodeError)}
                            autoFocus
                            margin="dense"
                            fullWidth
                            variant="standard"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose}>{t("twoFactor.cancel")}</Button>
                        <Button
                            variant="contained"
                            type="submit"
                        >
                            {t("twoFactor.button")}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};

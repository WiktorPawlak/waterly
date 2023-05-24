import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField,} from "@mui/material";
import {Box} from "@mui/system";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import {LoginRequestBody, LoginResponse, postLogin,} from "../../../../api/authApi";
import {useAccount} from "../../../../hooks/useAccount";
import {enqueueSnackbar} from "notistack";
import {resolveApiError} from "../../../../api/apiErrors";

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
    const [twoFactorCode, setTwoFactor] = useState("");
    const {addUserToStorage} = useAccount();
    const {t} = useTranslation();

    const handleClose = () => {
        setIsOpen(false);
    };

    const handleSubmit = async () => {
        const loginDto: LoginRequestBody = {
            login: login,
            password: password,
            twoFACode: twoFactorCode,
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
                <DialogTitle>{t("twoFactor.header")}</DialogTitle>
                <DialogContent>
                    <DialogContentText>{t("twoFactor.description")}</DialogContentText>
                    <TextField
                        onChange={(e) => setTwoFactor(e.target.value)}
                        autoFocus
                        margin="dense"
                        type="email"
                        fullWidth
                        variant="standard"
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>{t("twoFactor.cancel")}</Button>
                    <Button
                        variant="contained"
                        disabled={twoFactorCode.length === 0}
                        onClick={handleSubmit}
                    >
                        {t("twoFactor.button")}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

import { zodResolver } from "@hookform/resolvers/zod";
import { Button, Dialog, DialogTitle, DialogContent, DialogContentText, TextField, DialogActions } from "@mui/material";
import { Box } from "@mui/system";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { postSendResetPasswordEmail } from "../../../../api/accountApi";
import { useToast } from "../../../../hooks/useToast";
import { resetPasswordEmailSchema } from "../../../../validation/validationSchemas";
import { Toast } from "../../Toast";
import { LoginRequestBody, LoginResponse, postLogin } from "../../../../api/authApi";
import jwt_decode from "jwt-decode";
import { useNavigate } from "react-router-dom";


interface Props {
    isOpen: boolean;
    login: string;
    password: string;
    setIsOpen: (isOpen: boolean) => void;
  }

export const EnterTwoFACodeModal = ({ isOpen, setIsOpen, login, password }: Props) => {
    const {
        register: registerEmail,
        handleSubmit: handleEmailSubmit,
        formState: { errors: emailErrors },
    } = useForm<resetPasswordEmailSchema>({
        resolver: zodResolver(resetPasswordEmailSchema),
    });

    const emailErrorMessage = emailErrors?.email?.message;
    const [twoFactorCode, setTwoFactor] = useState("");
    const toast = useToast();
    const { t } = useTranslation();
    const navigation = useNavigate();

    const handleClickOpen = () => {
        setIsOpen(true);
    };

    const handleClose = () => {
        setIsOpen(false);
    };

    const handleSubmit = async () => {
        const loginDto: LoginRequestBody = {
            login: login,
            password: password,
            twoFACode: twoFactorCode
        }
        const response: LoginResponse<string> | null = await postLogin(loginDto);  
        const token = response!.data as string;
        const decodedToken: any = jwt_decode(token);
        if (decodedToken) {
          const roles = decodedToken.roles;
          const username = decodedToken.jti;

          const user = {
            username,
            roles,
          };

          localStorage.setItem("jwtToken", token);
          localStorage.setItem("user", JSON.stringify(user));
          navigation("/profile");
          return true;
        }
    }

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
                    <DialogContentText>
                        {t("twoFactor.description")}
                    </DialogContentText>
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
                    <Button onClick={handleClose}>
                        {t("twoFactor.cancel")}
                    </Button>
                    <Button
                        variant="contained"
                        disabled={twoFactorCode.length === 0}
                        onClick={handleSubmit}
                    >
                        {t("twoFactor.button")}
                    </Button>
                </DialogActions>
            </Dialog>
            <Toast
                isToastOpen={toast.isToastOpen}
                setIsToastOpen={toast.setIsToastOpen}
                message={toast.message}
                severity={toast.severity}
            />
        </Box>
    );
};

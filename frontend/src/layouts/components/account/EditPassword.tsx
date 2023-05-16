import { Button, TextField, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, IconButton, InputAdornment } from "@mui/material";
import { Box } from "@mui/system";
import { useState } from "react"
import { useTranslation } from "react-i18next";
import { useToast } from "../../../hooks/useToast";
import { Toast } from "../Toast";
import { changeOwnPassword } from "../../../api/accountApi";
import React from "react";
import { resolveApiError } from "../../../api/apiErrors";
import { Label, Visibility, VisibilityOff } from "@mui/icons-material";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { ChangeOwnPasswordSchemaType, changeOwnPasswordSchema } from "../../../validation/validationSchemas";

export const EditPassword = () => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [open, setOpen] = React.useState(false);
    const [showOldPassword, setShowOldPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const toast = useToast();
    const { t } = useTranslation();

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<ChangeOwnPasswordSchemaType>({
        resolver: zodResolver(changeOwnPasswordSchema),
    });

    const oldPasswordErrorMessage = errors?.oldPassword?.message;
    const newPasswordErrorMessage = errors?.newPassword?.message;
    const passwordRepeatMessage = errors?.confirmPassword?.message;

    const handleClickShowOldPassword = () => {
        setShowOldPassword(!showOldPassword)
    }
    const handleClickShowNewPassword = () => {
        setShowNewPassword(!showNewPassword)
    }
    const handleClickShowConfirmPassword = () => {
        setShowConfirmPassword(!showConfirmPassword)
    }

    const handleMouseDownOldPassword = () => {
        setShowOldPassword(!showOldPassword);
    }
    const handleMouseDownNewPassword = () => {
        setShowNewPassword(!showNewPassword);
    }
    const handleMouseDownConfirmPassword = () => {
        setShowConfirmPassword(!showConfirmPassword);
    }

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setOldPassword("");
        setNewPassword("");
        setConfirmPassword("");
    };

    async function changeOwnPasswordHandle() {
        const accountPasswordDto = {
            oldPassword: oldPassword,
            newPassword: newPassword
        }
        const response = await changeOwnPassword(accountPasswordDto);
        if (response.status === 200) {
            toast.showSuccessToast(t("changePassword.success"));
            handleClose();
        }
        else {
            toast.showErrorToast(t(resolveApiError(response.error)));
        }
    }

    return (
        <Box
            sx={{
                display: "flex",
                flexDirection: "column",
                width: "100%",
            }}>
            <Button
                variant="contained"
                sx={{
                    textTransform: "none",
                    fontWeight: "700",
                    mb: { xs: 5, md: 1 },
                }}
                onClick={handleClickOpen}>
                {t("editAccountDetailsPage.ownPasswordChange.changePasswordButton")}
            </Button>
            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>{t("changePassword.header")}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {t("changePassword.description")}
                    </DialogContentText>
                    <TextField
                        {...register("oldPassword")}
                        error={!!oldPasswordErrorMessage}
                        helperText={oldPasswordErrorMessage && t(oldPasswordErrorMessage)}
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        autoFocus
                        margin="dense"
                        label={t("changePassword.passwords.oldPasswordLabel")}
                        type={showOldPassword ? "text" : "password"}
                        fullWidth
                        variant="standard"
                        sx={{ mb: { md: 2 } }}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <IconButton
                                        aria-label="toggle password visibility"
                                        onClick={handleClickShowOldPassword}
                                        onMouseDown={handleMouseDownOldPassword}
                                    >
                                        {showOldPassword ? <Visibility /> : <VisibilityOff />}
                                    </IconButton>
                                </InputAdornment>
                            )
                        }}
                    />
                    <TextField
                        {...register("newPassword")}
                        error={!!newPasswordErrorMessage}
                        helperText={newPasswordErrorMessage && t(newPasswordErrorMessage)}
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        autoFocus
                        margin="dense"
                        label={t("changePassword.passwords.newPasswordLabel")}
                        type={showNewPassword ? "text" : "password"}
                        fullWidth
                        variant="standard"
                        sx={{ mb: { md: 2 } }}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <IconButton
                                        aria-label="toggle password visibility"
                                        onClick={handleClickShowNewPassword}
                                        onMouseDown={handleMouseDownNewPassword}
                                    >
                                        {showNewPassword ? <Visibility /> : <VisibilityOff />}
                                    </IconButton>
                                </InputAdornment>
                            )
                        }}
                    />
                    <TextField
                        {...register("confirmPassword")}
                        error={!!passwordRepeatMessage}
                        helperText={passwordRepeatMessage && t(passwordRepeatMessage)}
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        autoFocus
                        margin="dense"
                        label={t("changePassword.passwords.confirmNewPasswordLabel")}
                        type={showConfirmPassword ? "text" : "password"}
                        fullWidth
                        variant="standard"
                        sx={{ mb: { md: 2 } }}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <IconButton
                                        aria-label="toggle password visibility"
                                        onClick={handleClickShowConfirmPassword}
                                        onMouseDown={handleMouseDownConfirmPassword}
                                    >
                                        {showConfirmPassword ? <Visibility /> : <VisibilityOff />}
                                    </IconButton>
                                </InputAdornment>
                            )
                        }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>
                        {t("changePassword.buttons.cancel")}
                    </Button>
                    <Button
                        disabled={
                            oldPassword.length === 0 ||
                            newPassword.length === 0 ||
                            confirmPassword.length === 0
                        }
                        // onClick={changeOwnPasswordHandle}
                        onClick={handleSubmit(async () => { await changeOwnPasswordHandle() })}
                    >
                        {t("changePassword.buttons.change")}
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
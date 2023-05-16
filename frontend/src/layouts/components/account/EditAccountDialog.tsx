import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import { Box } from "@mui/system";
import { useToast } from "../../../hooks/useToast";
import { useTranslation } from "react-i18next";
import { AccountDto, EditAccountDetailsDto, putAccountDetails } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import { Toast } from "../Toast";
import CloseIcon from "@mui/icons-material/Close";
import { StyledTextField } from "../../../pages/admin/AccountDetailsPage/AccountDetailsPage.styled";
import { useState } from "react";

interface Props {
    accountId: number,
    accountDto: AccountDto
    isOpen: boolean;
    etag: string,
    setIsOpen: (isOpen: boolean) => void;

}

export const EditAccountDialog = ({ accountId, accountDto, isOpen, etag, setIsOpen }: Props) => {
    const { t } = useTranslation();
    const toast = useToast();
    const [account, setAccount] = useState<AccountDto>(accountDto);

    const handleClose = () => {
        setIsOpen(false);
    };

    const handleConfirmAction = async () => {
        const response = await putAccountDetails(accountId, account, etag);

        if (response.status === 204) {
            toast.showSuccessToast(
                t("editAccountDialog.accountEditedSuccessfully")
            );
            setIsOpen(false);
        } else {
            toast.showErrorToast(t(resolveApiError(response.error)));
            setIsOpen(false);
        }
    };

    return (
        <Box>
            <Dialog open={isOpen} onClose={handleClose}>
                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                    }}
                >
                    <DialogTitle id="role-modal-title">{t("editAccountDialog.editAccount")}</DialogTitle>
                    <Button sx={{ width: "30px" }} onClick={handleClose}>
                        <CloseIcon />
                    </Button>
                </Box>
                <DialogContent sx={{ width: "400px" }}>
                    <Box sx={{ width: "100%" }}>
                        <StyledTextField
                            label={t("accountDetailsPage.detailsFields.firstName")}
                            variant="standard"
                            value={account.firstName}
                            onChange={(e) => {
                                setAccount({
                                    ...account,
                                    firstName: e.target.value,
                                    version: accountDto.version
                                });
                            }}
                        />
                        <StyledTextField
                            variant="standard"
                            label={t("accountDetailsPage.detailsFields.lastName")}
                            value={account.lastName}
                            onChange={(e) => {
                                setAccount({
                                    ...account,
                                    lastName: e.target.value,
                                    version: accountDto.version
                                });
                            }}
                            sx={{ mr: { xs: 0, md: 3 } }}
                        />
                        <StyledTextField
                            label={t("accountDetailsPage.detailsFields.phoneNumber")}
                            variant="standard"
                            value={account.phoneNumber}
                            onChange={(e) => {
                                setAccount({
                                    ...account,
                                    phoneNumber: e.target.value,
                                    version: accountDto.version
                                });
                            }}
                        />
                        <StyledTextField
                            label={t("accountDetailsPage.detailsFields.languageTag")}
                            variant="standard"
                            value={account.languageTag}
                            onChange={(e) => {
                                setAccount({
                                    ...account,
                                    languageTag: e.target.value,
                                    version: accountDto.version
                                });
                            }}
                            sx={{ mr: { xs: 0, md: 3 } }}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>{t("common.close")}</Button>
                    <Button variant="contained" color="primary" onClick={handleConfirmAction}>
                        {t("common.confirm")}
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
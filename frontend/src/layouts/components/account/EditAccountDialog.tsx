import {Button, Dialog, DialogActions, DialogContent, DialogTitle} from "@mui/material";
import {Box} from "@mui/system";
import {useToast} from "../../../hooks/useToast";
import {useTranslation} from "react-i18next";
import {AccountDto, EditAccountDetailsDto, putAccountDetails} from "../../../api/accountApi";
import {resolveApiError} from "../../../api/apiErrors";
import {Toast} from "../Toast";
import CloseIcon from "@mui/icons-material/Close";
import {StyledTextField} from "../../../pages/admin/AccountDetailsPage/AccountDetailsPage.styled";
import {useState} from "react";

interface Props {
    accountId: number,
    accountDto: AccountDto
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;

}

export const EditAccountDialog = ({accountId, accountDto, isOpen, setIsOpen}: Props) => {
    const {t} = useTranslation();
    const toast = useToast();

    const handleClose = () => {
        setIsOpen(false);
    };

    const [editAccountDto, setEditAccountDto] = useState<EditAccountDetailsDto>({
        firstName: accountDto.firstName,
        lastName: accountDto.lastName,
        phoneNumber: accountDto.phoneNumber,
        languageTag: accountDto.languageTag
    })

    const handleConfirmAction = async () => {
        const response = await putAccountDetails(accountId, editAccountDto);

        if (response.status === 204) {
            toast.showSuccessToast(
                t("editAccountDialog.accountEditedSuccessfully")
            );
            setIsOpen(false);
        } else {
            toast.showErrorToast(t(resolveApiError(response.error)));
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
                    <Button sx={{width: "30px"}} onClick={handleClose}>
                        <CloseIcon/>
                    </Button>
                </Box>
                <DialogContent sx={{width: "400px"}}>
                    <Box sx={{width: "100%"}}>
                        <StyledTextField
                            label={t("accountDetailsPage.detailsFields.firstName")}
                            variant="standard"
                            value={editAccountDto.firstName}
                            onChange={(e) => {
                                setEditAccountDto({
                                    ...editAccountDto,
                                    firstName: e.target.value,
                                });
                            }}
                        />
                        <StyledTextField
                            variant="standard"
                            label={t("accountDetailsPage.detailsFields.lastName")}
                            value={editAccountDto.lastName}
                            onChange={(e) => {
                                setEditAccountDto({
                                    ...editAccountDto,
                                    lastName: e.target.value,
                                });
                            }}
                            sx={{mr: {xs: 0, md: 3}}}
                        />
                        <StyledTextField
                            label={t("accountDetailsPage.detailsFields.phoneNumber")}
                            variant="standard"
                            value={editAccountDto.phoneNumber}
                            onChange={(e) => {
                                setEditAccountDto({
                                    ...editAccountDto,
                                    phoneNumber: e.target.value,
                                });
                            }}
                        />
                        <StyledTextField
                            label={t("accountDetailsPage.detailsFields.languageTag")}
                            variant="standard"
                            value={editAccountDto.languageTag}
                            onChange={(e) => {
                                setEditAccountDto({
                                    ...editAccountDto,
                                    languageTag: e.target.value,
                                });
                            }}
                            sx={{mr: {xs: 0, md: 3}}}
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
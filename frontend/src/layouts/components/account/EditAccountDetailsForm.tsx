import {Box, Button, Divider, TextField, ToggleButton, ToggleButtonGroup, Typography,} from "@mui/material";
import {AccountDto, editAccountDetails} from "../../../api/accountApi";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {editAccountDetailsSchema, EditAccountDetailsSchemaType,} from "../../../validation/validationSchemas";
import {EditEmail} from "./EditEmail";
import {resolveApiError} from "../../../api/apiErrors";
import {EditPassword} from "./EditPassword";
import {useSnackbar} from "notistack";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {languages} from "../../../types";

interface Props {
    account: AccountDto;
    fetchAccountDetails: VoidFunction;
}

export const EditAccountDetailsForm = ({
                                           account,
                                           fetchAccountDetails,
                                       }: Props) => {
    const {enqueueSnackbar} = useSnackbar();
    const {t} = useTranslation();
    const [languageTag, setLanguageTag] = useState(account.languageTag);
    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm<EditAccountDetailsSchemaType>({
        resolver: zodResolver(editAccountDetailsSchema),
        mode: "onChange",
        reValidateMode: "onChange",
        defaultValues: {
            firstName: account.firstName,
            lastName: account.lastName,
            phoneNumber: account.phoneNumber,
        },
    });

    const {
        firstName: firstNameError,
        lastName: lastNameError,
        phoneNumber: phoneNumberError,
    } = errors;
    const firstNameErrorMessage = firstNameError?.message;
    const lastNameErrorMessage = lastNameError?.message;
    const phoneNumberErrorMessage = phoneNumberError?.message;

    const handleEditButton = async (editAccountDto: any) => {
        if (editAccountDto && account) {
            const response = await editAccountDetails({
                ...editAccountDto,
                languageTag,
            });
            if (response.status === 204) {
                enqueueSnackbar(t("editAccountDetailsPage.alert.detailsSuccesEdited"), {
                    variant: "success",
                });
            } else {
                enqueueSnackbar(t(resolveApiError(response.error)), {
                    variant: "error",
                });
            }
            fetchAccountDetails();
        }
    };

    const handleLanguageTagChange = (value: string) => {
        if (value) {
            setLanguageTag(value);
        }
    };

    return (
        <Box
            sx={{
                display: "flex",
                flexDirection: "column",
                width: {xs: "100%", md: "50%"},
            }}
        >
            <EditPassword/>
            <EditEmail accountEmail={account.email}/>
            <Divider variant="middle" sx={{my: 2}}/>
            <form onSubmit={handleSubmit(handleEditButton)}>
                <Button
                    variant="contained"
                    sx={{
                        textTransform: "none",
                        fontWeight: "700",
                        mb: {xs: 5, md: 2},
                    }}
                    type="submit"
                >
                    {t("editAccountDetailsPage.detailsButton")}
                </Button>
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        width: "100%",
                    }}
                >
                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "space-between",
                        }}
                    >
                        <Typography
                            variant="h4"
                            sx={{fontSize: "16px", fontWeight: "700"}}
                        >
                            {t(
                                "editAccountDetailsPage.editAccountDetailEntry.firstNameLabel"
                            )}
                        </Typography>
                    </Box>
                    <TextField
                        {...register("firstName")}
                        error={!!firstNameErrorMessage}
                        helperText={firstNameErrorMessage && t(firstNameErrorMessage)}
                        variant="standard"
                        sx={{color: "text.secondary"}}
                    />
                </Box>

                <Divider variant="middle" sx={{my: 2}}/>

                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        width: "100%",
                    }}
                >
                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "space-between",
                        }}
                    >
                        <Typography
                            variant="h4"
                            sx={{fontSize: "16px", fontWeight: "700"}}
                        >
                            {t("editAccountDetailsPage.editAccountDetailEntry.lastNameLabel")}
                        </Typography>
                    </Box>
                    <TextField
                        {...register("lastName")}
                        error={!!lastNameErrorMessage}
                        helperText={lastNameErrorMessage && t(lastNameErrorMessage)}
                        variant="standard"
                        sx={{color: "text.secondary"}}
                    />
                </Box>

                <Divider variant="middle" sx={{my: 2}}/>

                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        width: "100%",
                    }}
                >
                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "space-between",
                        }}
                    >
                        <Typography
                            variant="h4"
                            sx={{fontSize: "16px", fontWeight: "700"}}
                        >
                            {t(
                                "editAccountDetailsPage.editAccountDetailEntry.phoneNumberLabel"
                            )}
                        </Typography>
                    </Box>
                    <TextField
                        {...register("phoneNumber")}
                        error={!!phoneNumberErrorMessage}
                        helperText={phoneNumberErrorMessage && t(phoneNumberErrorMessage)}
                        variant="standard"
                        sx={{color: "text.secondary"}}
                    />
                </Box>
                <Divider variant="middle" sx={{my: 2}}/>
            </form>
            <Box
                sx={{
                    display: "flex",
                    flexDirection: "column",
                    width: "100%",
                }}
            >
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "row",
                        justifyContent: "space-between",
                    }}
                >
                    <Typography variant="h4" sx={{fontSize: "16px", fontWeight: "700"}}>
                        {t(
                            "editAccountDetailsPage.editAccountDetailEntry.preferedLanguageLabel"
                        )}
                    </Typography>
                </Box>

                <ToggleButtonGroup
                    color="standard"
                    value={languageTag}
                    exclusive
                    onChange={(e, value) => {
                        handleLanguageTagChange(value);
                    }}
                    aria-label="Platform"
                >
                    <ToggleButton value={languages.pl}>{t("language.pl")}</ToggleButton>
                    <ToggleButton value={languages.en}>{t("language.en")}</ToggleButton>
                </ToggleButtonGroup>
            </Box>
            <Divider variant="middle" sx={{my: 2}}/>
        </Box>
    );
};

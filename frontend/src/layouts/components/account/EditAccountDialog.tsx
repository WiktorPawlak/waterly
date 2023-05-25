import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from "@mui/material";
import { Box } from "@mui/system";
import { useTranslation } from "react-i18next";
import { AccountDto, putAccountDetails } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import CloseIcon from "@mui/icons-material/Close";
import { StyledTextField } from "../../../pages/admin/AccountDetailsPage/AccountDetailsPage.styled";
import { useState } from "react";
import { enqueueSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  EditAccountSchemaType,
  editAccountDetailsSchema,
} from "../../../validation/validationSchemas";
import { languages } from "../../../types";

interface Props {
  accountId: number;
  accountDto: AccountDto;
  isOpen: boolean;
  etag: string;
  setIsOpen: (isOpen: boolean) => void;
}

export const EditAccountDialog = ({
  accountId,
  accountDto,
  isOpen,
  etag,
  setIsOpen,
}: Props) => {
  const { t } = useTranslation();
  const [languageTag, setLanguageTag] = useState(accountDto.languageTag);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<EditAccountSchemaType>({
    resolver: zodResolver(editAccountDetailsSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      firstName: accountDto.firstName,
      lastName: accountDto.lastName,
      phoneNumber: accountDto.phoneNumber,
    },
  });

  const handleClose = () => {
    setIsOpen(false);
    reset();
  };

  const {
    firstName: firstNameError,
    lastName: lastNameError,
    phoneNumber: phoneNumberError,
  } = errors;
  const firstNameErrorMessage = firstNameError?.message;
  const lastNameErrorMessage = lastNameError?.message;
  const phoneNumberErrorMessage = phoneNumberError?.message;

  const handleLanguageTagChange = (value: string) => {
    if (value) {
      setLanguageTag(value);
    }
  };

  const handleConfirmAction = async (editAccount: any) => {
    const response = await putAccountDetails(
      accountId,
      {
        ...editAccount,
        languageTag,
        id: accountDto.id,
        version: accountDto.version,
        twoFAEnabled: accountDto.twoFAEnabled,
      },
      etag
    );

    if (response.status === 204) {
      enqueueSnackbar(t("editAccountDialog.accountEditedSuccessfully"), {
        variant: "success",
      });
      setIsOpen(false);
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
              {t("editAccountDialog.editAccount")}
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
                label={t("accountDetailsPage.detailsFields.firstName")}
                variant="standard"
                sx={{ width: "100% !important" }}
                {...register("firstName")}
                error={!!firstNameErrorMessage}
                helperText={firstNameErrorMessage && t(firstNameErrorMessage)}
              />
              <StyledTextField
                variant="standard"
                sx={{ width: "100% !important" }}
                label={t("accountDetailsPage.detailsFields.lastName")}
                {...register("lastName")}
                error={!!lastNameErrorMessage}
                helperText={lastNameErrorMessage && t(lastNameErrorMessage)}
              />
              <StyledTextField
                label={t("accountDetailsPage.detailsFields.phoneNumber")}
                sx={{ width: "100% !important" }}
                variant="standard"
                {...register("phoneNumber")}
                error={!!phoneNumberErrorMessage}
                helperText={
                  phoneNumberErrorMessage && t(phoneNumberErrorMessage)
                }
              />
              <Typography
                variant="h4"
                sx={{
                  fontSize: "13px",
                  fontWeight: "500",
                  marginBottom: "10px",
                }}
              >
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.preferedLanguageLabel"
                )}
              </Typography>
              <ToggleButtonGroup
                color="standard"
                value={languageTag}
                exclusive
                onChange={(e, value) => {
                  handleLanguageTagChange(value);
                }}
                aria-label="Platform"
              >
                <ToggleButton value={languages.pl}>
                  {t("language.pl")}
                </ToggleButton>
                <ToggleButton value={languages.en}>
                  {t("language.en")}
                </ToggleButton>
              </ToggleButtonGroup>
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

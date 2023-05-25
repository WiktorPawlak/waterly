import { AccountDto, editEmailByAdmin } from "../../../api/accountApi";
import { useSnackbar } from "notistack";
import { useTranslation } from "react-i18next";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  EditEmailByAdminSchemaType,
  editEmailByAdminSchema,
} from "../../../validation/validationSchemas";
import { Box } from "@mui/system";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { StyledTextField } from "../../../pages/admin/AccountDetailsPage/AccountDetailsPage.styled";
import { resolveApiError } from "../../../api/apiErrors";

interface Props {
  account: AccountDto;
  etag: string;
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const EditEmailByAdminDialog = ({
  account,
  etag,
  isOpen,
  setIsOpen,
}: Props) => {
  const { enqueueSnackbar } = useSnackbar();
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<EditEmailByAdminSchemaType>({
    resolver: zodResolver(editEmailByAdminSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      email: account.email,
    },
  });

  const handleClose = () => {
    setIsOpen(false);
    reset();
  };

  const handleConfirmAction = async (dto: any) => {
    if (account.email != dto.email.toLowerCase()) {
      const response = await editEmailByAdmin(
        {
          ...dto,
          version: account.version,
          id: account.id,
        },
        etag
      );

      if (response.status === 204) {
        enqueueSnackbar(t("editAccountDetailsPage.alert.emailToUserSended"), {
          variant: "success",
        });
        setIsOpen(false);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
    } else {
      enqueueSnackbar(t("editAccountDetailsPage.alert.emailNotChanged"), {
        variant: "warning",
      });
    }
  };

  const emailErrorMessage = errors?.email?.message;

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
                sx={{ width: "100% !important" }}
                label={t("accountDetailsPage.detailsFields.firstName")}
                variant="standard"
                {...register("email")}
                error={!!emailErrorMessage}
                helperText={emailErrorMessage && t(emailErrorMessage)}
              />
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

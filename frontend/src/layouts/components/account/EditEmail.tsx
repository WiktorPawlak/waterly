import { Box, Button, TextField, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import { EditEmailDto, editEmail } from "../../../api/accountApi";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  EditEmailSchemaType,
  editEmailSchema,
} from "../../../validation/validationSchemas";
import { resolveApiError } from "../../../api/apiErrors";
import { ResendEditMailDialog } from "./ResendEditMailDialog";
import { useSnackbar } from "notistack";

interface Props {
  accountEmail: string;
}

export const EditEmail = ({ accountEmail }: Props) => {
  const [isOpen, setIsOpen] = useState(false);
  const { enqueueSnackbar } = useSnackbar();
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EditEmailSchemaType>({
    resolver: zodResolver(editEmailSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      email: accountEmail,
    },
  });

  const emailErrorMessage = errors?.email?.message;

  const handleEditEmailButton = async (dto: EditEmailDto) => {
    if (accountEmail != dto.email.toLowerCase()) {
      const response = await editEmail(dto);

      if (response.status === 204) {
        setIsOpen(true);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
    }
  };

  return (
    <>
      <ResendEditMailDialog isOpen={isOpen} setIsOpen={setIsOpen} />
      <form onSubmit={handleSubmit(handleEditEmailButton)}>
        <Button
          variant="contained"
          sx={{
            textTransform: "none",
            fontWeight: "700",
            mb: { xs: 5, md: 2 },
          }}
          type="submit"
        >
          {t("editAccountDetailsPage.emailButton")}
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
              sx={{ fontSize: "16px", fontWeight: "700" }}
            >
              {t("editAccountDetailsPage.editAccountDetailEntry.emailLabel")}
            </Typography>
          </Box>
          <TextField
            {...register("email")}
            error={!!emailErrorMessage}
            helperText={emailErrorMessage && t(emailErrorMessage)}
            variant="standard"
            sx={{ color: "text.secondary" }}
          />
        </Box>
      </form>
    </>
  );
};

import { Box, Button, TextField, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import { AccountDto, editEmail } from "../../../api/accountApi";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  EditEmailSchemaType,
  editEmailSchema,
} from "../../../validation/validationSchemas";
import { resolveApiError } from "../../../api/apiErrors";
import { useToast } from "../../../hooks/useToast";
import { Toast } from "../Toast";
import { ResendEditMailDialog } from "./ResendEditMailDialog";

interface Props {
  accountDetails: AccountDto;
}

export const EditEmail = ({ accountDetails }: Props) => {
  const [email, setEmail] = useState(accountDetails.email);
  const [isOpen, setIsOpen] = useState(false);
  const toast = useToast();
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EditEmailSchemaType>({
    resolver: zodResolver(editEmailSchema),
  });

  const emailErrorMessage = errors?.email?.message;

  const handleEditEmailButton = async () => {
    if (accountDetails.email != email.toLowerCase()) {
      const response = await editEmail({ email });

      if (response.status === 204) {
        setIsOpen(true);
      } else {
        toast.showErrorToast(t(resolveApiError(response.error)));
      }
    }
  };

  return (
    <>
      <ResendEditMailDialog isOpen={isOpen} setIsOpen={setIsOpen} />
      <Button
        variant="contained"
        sx={{
          textTransform: "none",
          fontWeight: "700",
          mb: { xs: 5, md: 2 },
        }}
        onClick={handleSubmit(handleEditEmailButton)}
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
          <Typography variant="h4" sx={{ fontSize: "16px", fontWeight: "700" }}>
            {t("editAccountDetailsPage.editAccountDetailEntry.emailLabel")}
          </Typography>
        </Box>
        <TextField
          {...register("email")}
          error={!!emailErrorMessage}
          helperText={emailErrorMessage && t(emailErrorMessage)}
          variant="standard"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          sx={{ color: "text.secondary" }}
        />
      </Box>
      <Toast
        isToastOpen={toast.isToastOpen}
        setIsToastOpen={toast.setIsToastOpen}
        message={toast.message}
        severity={toast.severity}
      />
    </>
  );
};

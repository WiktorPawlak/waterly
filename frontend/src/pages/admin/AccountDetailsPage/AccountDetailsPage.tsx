import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AccountDto, getUserById } from "../../../api/accountApi";
import { MainLayout } from "../../../layouts/MainLayout";
import { Box, Button, CircularProgress, Typography } from "@mui/material";
import { StyledTextField } from "./AccountDetailsPage.styled";
import { useTranslation } from "react-i18next";
import { EditRolesModal } from "../../../layouts/components/account/EditRolesModal";
import { EditAccountDialog } from "../../../layouts/components/account/EditAccountDialog";
import { EditPasswordByAdmin } from "../../../layouts/components/account/EditPasswordByAdmin";
import { Toast } from "../../../layouts/components/Toast";
import { useToast } from "../../../hooks/useToast";
import { resolveApiError } from "../../../api/apiErrors";

const AccountDetailsPage = () => {
  const { id } = useParams();
  const [account, setAccount] = useState<AccountDto>();
  const [addRoleModalOpen, setAddRoleModalOpen] = useState(false);
  const [removeRoleModalOpen, setRemoveRoleModalOpen] = useState(false);
  const [etag, setEtag] = useState("");
  const [editAccountDialogOpen, setEditAccountDialogOpen] = useState(false);
  const [editPasswordDialogOpen, setEditPasswordDialogOpen] = useState(false);
  const { t } = useTranslation();
  const toast = useToast();


  function translateRoles(roles: string[]): string[] {
    const convertedRoles = roles.map((role) => {
      return t("roles." + role);
    });

    return convertedRoles;
  }

  useEffect(() => {
    if (id) {
      getUserById(parseInt(id)).then((response) => {
        if (response.data) {
          setAccount(response.data!);
          setEtag(response.headers!['etag'] as string);
        } else {
          toast.showErrorToast(t(resolveApiError(response.error)));
        }
      });
    }
  }, [addRoleModalOpen, removeRoleModalOpen, editAccountDialogOpen, editPasswordDialogOpen]);

  if (!account) {
    return <CircularProgress />;
  }

  return (
    <MainLayout isOverflowHidden={false}>
      <EditRolesModal accountId={account.id} accountRoles={account.roles} isOpen={addRoleModalOpen} isGrant={true} setIsOpen={setAddRoleModalOpen} />
      <EditRolesModal accountId={account.id} accountRoles={account.roles} isOpen={removeRoleModalOpen} isGrant={false} setIsOpen={setRemoveRoleModalOpen} />
      <EditAccountDialog accountId={account.id} accountDto={account} isOpen={editAccountDialogOpen} etag={etag} setIsOpen={setEditAccountDialogOpen} />
      <EditPasswordByAdmin isOpen={editPasswordDialogOpen} setIsOpen={setEditPasswordDialogOpen} email={account.email} />
      <Box
        sx={{
          height: "100vh",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("accountDetailsPage.headers.detailsName")}
        </Typography>
        <Typography sx={{ mb: 3, color: "text.secondary" }}>
          {t("accountDetailsPage.headers.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            mb: { xs: 6, md: 10 },
          }}
        >
          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
            onClick={() =>
              setAddRoleModalOpen(true)}
          >
            {t("accountDetailsPage.actions.addRole")}
          </Button>
          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
            onClick={() =>
              setRemoveRoleModalOpen(true)}
          >
            {t("accountDetailsPage.actions.removeRole")}
          </Button>

          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
            onClick={() =>
              setEditAccountDialogOpen(true)}
          >
            {t("accountDetailsPage.actions.edit")}
          </Button>
          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
            onClick={() => setEditPasswordDialogOpen(true)}
          >
            {t("accountDetailsPage.actions.changePassword")}
          </Button>
        </Box>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            width: "100%",
          }}
        >
          <Box sx={{ width: "100%" }}>
            <StyledTextField
              variant="standard"
              label="ID"
              value={account.id}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.login")}
              variant="standard"
              value={account.login}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.email")}
              variant="standard"
              value={account.email}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.firstName")}
              variant="standard"
              value={account.firstName}
            />
            <StyledTextField
              variant="standard"
              label={t("accountDetailsPage.detailsFields.lastName")}
              value={account.lastName}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.phoneNumber")}
              variant="standard"
              value={account.phoneNumber}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.languageTag")}
              variant="standard"
              value={account.languageTag}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.roles")}
              variant="standard"
              value={translateRoles(account.roles)}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.active")}
              variant="standard"
              value={account.active}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.createdOn")}
              variant="standard"
              value={account.createdOn}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.createdBy")}
              variant="standard"
              value={account.createdBy}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.updatedOn")}
              variant="standard"
              value={account.updatedOn}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.updatedBy")}
              variant="standard"
              value={account.updatedBy}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.lastSuccessAuth")}
              variant="standard"
              value={account.lastSuccessAuth}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.lastIncorrectAuth")}
              variant="standard"
              value={account.lastIncorrectAuth}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.lastIpAddress")}
              variant="standard"
              value={account.lastIpAddress}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.incorrectAuthCount")}
              variant="standard"
              value={account.incorrectAuthCount}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label={t("accountDetailsPage.detailsFields.twoFaEnabled")}
              variant="standard"
              value={account.twoFAEnabled}
            />
          </Box>
          <Toast
            isToastOpen={toast.isToastOpen}
            setIsToastOpen={toast.setIsToastOpen}
            message={toast.message}
            severity={toast.severity}
          />
        </Box>
      </Box>
    </MainLayout>
  );
};

export default AccountDetailsPage;

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  List,
  ListItemButton,
} from "@mui/material";
import { Box } from "@mui/system";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { EditRolesDto, grantAccountPermissions } from "../../../api/accountApi";
import { resolveApiError } from "../../../api/apiErrors";
import { RoleOperation } from "../../../types";
import CloseIcon from "@mui/icons-material/Close";
import { enqueueSnackbar } from "notistack";

interface Props {
  accountId: number;
  accountRoles: string[];
  isOpen: boolean;
  isGrant: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const EditRolesModal = ({
  accountId,
  accountRoles,
  isOpen,
  isGrant,
  setIsOpen,
}: Props) => {
  const { t } = useTranslation();

  const [selectedRole, setSelectedRole] = useState("");
  const availableRolesToGrant = [
    "ADMINISTRATOR",
    "FACILITY_MANAGER",
    "OWNER",
  ].filter((role) => !accountRoles.includes(role));
  const availableRolesToRemove = [
    "ADMINISTRATOR",
    "FACILITY_MANAGER",
    "OWNER",
  ].filter((role) => accountRoles.includes(role));
  const availableRoles = isGrant
    ? availableRolesToGrant
    : availableRolesToRemove;

  const handleChooseRole = (role: string) => {
    setSelectedRole(role);
  };

  const handleClose = () => {
    setIsOpen(false);
  };

  const handleConfirmAction = async () => {
    const editRolesDto: EditRolesDto = {
      operation: isGrant ? RoleOperation.GRANT : RoleOperation.REVOKE,
      roles: [selectedRole],
    };
    const response = await grantAccountPermissions(accountId, editRolesDto);

    if (response.status === 200) {
      enqueueSnackbar(
        isGrant
          ? t("rolesModal.roleAddedSuccessfully")
          : t("rolesModal.roleRemovedSuccessfully"),
        {
          variant: "success",
        }
      );
      setIsOpen(false);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  return (
    <Box>
      <Dialog
        open={isOpen && availableRoles.length !== 0}
        onClose={handleClose}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <DialogTitle id="role-modal-title">
            {t("rolesModal.selectRole")}
          </DialogTitle>
          <Button sx={{ width: "30px" }} onClick={handleClose}>
            <CloseIcon />
          </Button>
        </Box>
        <DialogContent sx={{ width: "400px" }}>
          <List>
            {availableRoles.map((role) => (
              <ListItemButton
                key={role}
                selected={selectedRole === role}
                onClick={() => handleChooseRole(role)}
              >
                {t(`roles.${role}`)}
              </ListItemButton>
            ))}
          </List>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("common.close")}</Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleConfirmAction}
          >
            {t("common.confirm")}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

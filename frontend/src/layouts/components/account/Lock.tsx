import {useState} from "react";
import Button from "@mui/material/Button";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import {AccountActiveStatusDto, changeAccountActiveStatus,} from "../../../api/accountApi";
import {useSnackbar} from "notistack";
import {useTranslation} from "react-i18next";

export const Lock = (accountId: any) => {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const [isLocked, setIsLocked] = useState(true);

  const handleLockClick = async () => {
    const accountActiveStatusDto: AccountActiveStatusDto = {
      active: !isLocked,
    };
    await changeAccountActiveStatus(
      accountId.accountId,
      accountActiveStatusDto
    ).then((response) => {
      if (response.status === 200) {
        if (isLocked) {
          enqueueSnackbar(t("manageUsersPage.toastActivateSuccess"), {
            variant: "success",
          });
        } else {
          enqueueSnackbar(t("manageUsersPage.toastDeactivateSuccess"), {
            variant: "success",
          });
        }
        setIsLocked(!isLocked);
      } else {
        enqueueSnackbar(t("manageUsersPage.toastFailure"), {
          variant: "error",
        });
        console.error(response.error);
      }
    });
  };

  const renderLockIcon = () => {
    if (isLocked) {
      return <LockOutlinedIcon sx={{ color: "red" }} />;
    } else {
      return <LockOpenIcon sx={{ color: "green" }} />;
    }
  };

  return (
    <Button variant="text" onClick={handleLockClick}>
      {renderLockIcon()}
    </Button>
  );
};

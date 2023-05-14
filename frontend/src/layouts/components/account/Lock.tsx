import { useState } from "react";
import Button from "@mui/material/Button";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import {
  AccountActiveStatusDto,
  changeAccountActiveStatus,
} from "../../../api/accountApi";

export const Lock = (accountId: any) => {
  const [isLocked, setIsLocked] = useState(true);

  const handleLockClick = async () => {
    const accountActiveStatusDto: AccountActiveStatusDto = {
      active: !isLocked,
    };
    console.log(accountId);
    const response = await changeAccountActiveStatus(
      accountId.accountId,
      accountActiveStatusDto
    );
    if (response.status === 200) {
      setIsLocked(!isLocked);
    } else {
      console.error(response.error);
    }
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

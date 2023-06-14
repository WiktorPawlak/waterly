import { IconButton, Menu, MenuItem } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { Box } from '@mui/system';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';

interface Props {
  waterMeterId: number;
  handleReplaceButtonClick: (id: number) => void;
  handleEditButtonClick: (id: number) => void;
}

export const ThreeDotWaterMeterMenu = ({
  waterMeterId,
  handleEditButtonClick,
  handleReplaceButtonClick
}: Props) => {
  const { t } = useTranslation();

  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event: any) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <Box>
      <IconButton
        aria-label="more"
        aria-controls="three-dot-menu"
        aria-haspopup="true"
        onClick={handleClick}
      >
        <MoreVertIcon />
      </IconButton>
      <Menu
        id="three-dot-menu"
        anchorEl={anchorEl}
        keepMounted
        open={open}
        onClose={handleClose}
      >
        <MenuItem 
          onClick={() => handleEditButtonClick(waterMeterId)}
        >
          {t("threeDotWaterMeterMenu.edit")}
        </MenuItem>
        <MenuItem 
          onClick={() => handleReplaceButtonClick(waterMeterId)}
        >
          {t("threeDotWaterMeterMenu.replace")}
        </MenuItem>
      </Menu>
    </Box>
  );
}


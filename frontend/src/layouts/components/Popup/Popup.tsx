import { Popover, Box, Button, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { useTranslation } from "react-i18next";

export interface PopoverProps {
  popoverContent: JSX.Element;
  isOpen: boolean;
  onClose: () => void;
  anchorEl?: HTMLElement | null;
}

export const Popup = ({
  popoverContent,
  isOpen,
  onClose,
  anchorEl,
}: PopoverProps) => {
  const { t } = useTranslation();
  return (
    <Popover
      open={isOpen}
      onClose={onClose}
      anchorEl={anchorEl}
      anchorOrigin={{
        vertical: "bottom",
        horizontal: "center",
      }}
      transformOrigin={{
        vertical: "center",
        horizontal: "center",
      }}
    >
      <Box
        sx={{ display: "flex", flexDirection: "column", p: 3, width: "300px" }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            mb: 2,
          }}
        >
          <Typography sx={{ fontWeight: "700", mr: 1 }}>
            {t("popup.msg")}
          </Typography>
          <Button sx={{ width: "30px" }} onClick={onClose}>
            <CloseIcon />
          </Button>
        </Box>
        {popoverContent}
      </Box>
    </Popover>
  );
};

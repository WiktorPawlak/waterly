import { IconButton } from "@mui/material";
import { Close as IconClose } from "@mui/icons-material";
import { SnackbarKey, useSnackbar } from "notistack";

interface Props {
  snackbarKey: SnackbarKey;
}

export const SnackbarCloseButton = ({ snackbarKey }: Props) => {
  const { closeSnackbar } = useSnackbar();

  return (
    <IconButton onClick={() => closeSnackbar(snackbarKey)}>
      <IconClose style={{ color: "white" }} />
    </IconButton>
  );
};

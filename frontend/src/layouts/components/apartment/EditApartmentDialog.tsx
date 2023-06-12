import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import { Box } from "@mui/system";
import { useTranslation } from "react-i18next";
import CloseIcon from "@mui/icons-material/Close";
import {
  editApartmentSchema,
  EditApartmentSchemaType,
} from "../../../validation/validationSchemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { resolveApiError } from "../../../api/apiErrors";
import { enqueueSnackbar } from "notistack";
import { ApartmentDto, editApartment } from "../../../api/apartmentApi";
import { useEffect } from "react";

interface Props {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  apartment: ApartmentDto | undefined;
}

export const EditApartmentDialog = ({
  isOpen,
  setIsOpen,
  apartment,
}: Props) => {
  const { t } = useTranslation();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
  } = useForm<EditApartmentSchemaType>({
    resolver: zodResolver(editApartmentSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      number: apartment?.number,
      area: apartment?.area.toString(),
    },
  });

  const handleClose = () => {
    setIsOpen(false);
    handleReset();
  };

  const handleReset = () => {
    reset();
  };

  const { number: numberError, area: areaError } = errors;

  const numberErrorMessage = numberError?.message;
  const areaErrorMessage = areaError?.message;

  useEffect(() => {
    if (apartment) {
      setValue("area", apartment?.area.toString());
      setValue("number", apartment?.number);
    }
  }, [apartment]);

  const handleFormSubmit = async (formData: EditApartmentSchemaType) => {
    const response = await editApartment(apartment!!.id, {
      number: formData.number,
      area: parseFloat(formData.area),
    });

    if (response.status === 204) {
      enqueueSnackbar(t("apartmentPage.apartmentEditedSuccessfully"), {
        variant: "success",
      });
      handleClose();
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  return (
    <Box
      sx={{
        flexDirection: "column",
        display: "flex",
        width: "60%",
      }}
    >
      <Box
        sx={{
          mt: { xs: 40, md: 0 },
          display: "flex",
          flexDirection: "column",
          alignItems: { xs: "center", md: "flex-start" },
          justifyContent: { xs: "center", md: "flex-start" },
        }}
      ></Box>
      <Box>
        <Dialog open={isOpen} onClose={handleClose}>
          <form onSubmit={handleSubmit(handleFormSubmit)}>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <DialogTitle id="role-modal-title">
                {t("apartmentPage.editApartment")}
              </DialogTitle>
              <Button sx={{ width: "30px" }} onClick={handleClose}>
                <CloseIcon />
              </Button>
            </Box>
            <DialogContent sx={{ width: "400px" }}>
              <Box sx={{ width: "100%" }}>
                <Box
                  sx={{
                    display: "flex",
                    flexDirection: { xs: "column", md: "row" },
                  }}
                >
                  <TextField
                    label={t("apartmentPage.dataGrid.header.number")}
                    {...register("number")}
                    error={!!numberErrorMessage}
                    helperText={numberErrorMessage && t(numberErrorMessage)}
                    variant="standard"
                    name="number"
                    sx={{
                      mb: 3,
                      mr: { xs: 0, md: 5 },
                      "& label": {
                        color: "text.secondary",
                      },
                      "& label.Mui-focused": {
                        color: "primary.main",
                      },
                    }}
                  />
                  <TextField
                    label={t("apartmentPage.dataGrid.header.area")}
                    {...register("area")}
                    error={!!areaErrorMessage}
                    helperText={areaErrorMessage && t(areaErrorMessage)}
                    InputProps={{
                      inputProps: { step: 0.5, min: 1.0, max: 999.99 },
                    }}
                    variant="standard"
                    name="area"
                    type="number"
                    sx={{
                      mb: 3,
                      "& label": {
                        color: "text.secondary",
                      },
                      "& label.Mui-focused": {
                        color: "primary.main",
                      },
                    }}
                  />
                </Box>
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
    </Box>
  );
};
function setValue(arg0: string, arg1: any) {
  throw new Error("Function not implemented.");
}

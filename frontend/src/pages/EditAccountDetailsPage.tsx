import { MainLayout } from "../layouts/MainLayout";
import {
  Box,
  Button,
  CircularProgress,
  Divider,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from "@mui/material";
import loginPose from "../assets/loginPose.svg";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import {
  AccountDto,
  getSelfAccountDetails,
  editAccountDetails,
} from "../api/accountApi";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  EditAccountDetailsSchemaType,
  editAccountDetailsSchema,
} from "../validation/validationSchemas";
import { EditEmail } from "../layouts/components/account/EditEmail";
import { useToast } from "../hooks/useToast";
import { Toast } from "../layouts/components/Toast";
import { resolveApiError } from "../api/apiErrors";

const EditAccountDetailsPage = () => {
  const [accountDetails, setAccountDetails] = useState<AccountDto>();
  const toast = useToast();
  const { t } = useTranslation();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EditAccountDetailsSchemaType>({
    resolver: zodResolver(editAccountDetailsSchema),
  });

  const {
    firstName: firstNameError,
    lastName: lastNameError,
    phoneNumber: phoneNumberError,
  } = errors;
  const firstNameErrorMessage = firstNameError?.message;
  const lastNameErrorMessage = lastNameError?.message;
  const phoneNumberErrorMessage = phoneNumberError?.message;

  const fetchAccountDetails = () => {
    getSelfAccountDetails().then((it) => {
      if (it.data) {
        setAccountDetails(it.data);
      } else {
        console.error(it.error);
      }
    });
  };

  useEffect(() => {
    fetchAccountDetails();
  }, []);

  const handleEditButton = async () => {
    if (accountDetails) {
      const reponse = await editAccountDetails(accountDetails);
      console.log(reponse);
      if (reponse.status === 204) {
        toast.showSuccessToast(
          t("editAccountDetailsPage.alert.detailsSuccesEdited")
        );
      } else {
        toast.showErrorToast(t(resolveApiError(reponse.error)));
      }
      fetchAccountDetails();
    }
  };

  if (!accountDetails) {
    return <CircularProgress />;
  }

  return (
    <MainLayout>
      <Box
        sx={{
          position: "relative",
          height: "100vh",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("editAccountDetailsPage.header")}
        </Typography>

        <Typography sx={{ mb: { xs: 5, md: 5 }, color: "text.secondary" }}>
          {t("manageUsersPage.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            width: { xs: "100%", md: "50%" },
          }}
        >
          <EditEmail accountDetails={accountDetails} />
          <Divider variant="middle" sx={{ my: 2 }} />
          <Button
            variant="contained"
            sx={{
              textTransform: "none",
              fontWeight: "700",
              mb: { xs: 5, md: 6 },
            }}
            onClick={handleSubmit(handleEditButton)}
          >
            {t("editAccountDetailsPage.detailsButton")}
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
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.firstNameLabel"
                )}
              </Typography>
            </Box>
            <TextField
              {...register("firstName")}
              error={!!firstNameErrorMessage}
              helperText={firstNameErrorMessage && t(firstNameErrorMessage)}
              variant="standard"
              value={accountDetails.firstName}
              sx={{ color: "text.secondary" }}
              onChange={(e) => {
                setAccountDetails({
                  ...accountDetails,
                  firstName: e.target.value,
                });
              }}
            />
          </Box>

          <Divider variant="middle" sx={{ my: 2 }} />

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
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.lastNameLabel"
                )}
              </Typography>
            </Box>
            <TextField
              {...register("lastName")}
              error={!!lastNameErrorMessage}
              helperText={lastNameErrorMessage && t(lastNameErrorMessage)}
              variant="standard"
              value={accountDetails.lastName}
              sx={{ color: "text.secondary" }}
              onChange={(e) => {
                setAccountDetails({
                  ...accountDetails,
                  lastName: e.target.value,
                });
              }}
            />
          </Box>

          <Divider variant="middle" sx={{ my: 2 }} />

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
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.phoneNumberLabel"
                )}
              </Typography>
            </Box>
            <TextField
              {...register("phoneNumber")}
              error={!!phoneNumberErrorMessage}
              helperText={phoneNumberErrorMessage && t(phoneNumberErrorMessage)}
              variant="standard"
              value={accountDetails.phoneNumber}
              sx={{ color: "text.secondary" }}
              onChange={(e) => {
                setAccountDetails({
                  ...accountDetails,
                  phoneNumber: e.target.value,
                });
              }}
            />
          </Box>
          <Divider variant="middle" sx={{ my: 2 }} />

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
                {t(
                  "editAccountDetailsPage.editAccountDetailEntry.preferedLanguageLabel"
                )}
              </Typography>
            </Box>
            <ToggleButtonGroup
              color="standard"
              value={accountDetails.languageTag}
              exclusive
              onChange={(e, value) => {
                setAccountDetails({
                  ...accountDetails,
                  languageTag: value,
                });
              }}
              aria-label="Platform"
            >
              <ToggleButton value="pl-PL">{t("language.pl")}</ToggleButton>
              <ToggleButton value="en-US">{t("language.en")}</ToggleButton>
            </ToggleButtonGroup>
          </Box>
          <Divider variant="middle" sx={{ my: 2 }} />
        </Box>
        <Box
          sx={{
            width: { xs: "500px", md: "800px" },
            height: { xs: "500px", md: "800px" },
            position: "absolute",
            bottom: 0,
            right: { xs: "50%", md: -300 },
            transform: { xs: "translateX(-50%)", md: "translateX(0)" },
            top: { xs: "50%", md: 50 },
          }}
        >
          <img
            src={loginPose}
            alt="XD"
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
        </Box>
      </Box>
      <Toast
        isToastOpen={toast.isToastOpen}
        setIsToastOpen={toast.setIsToastOpen}
        message={toast.message}
        severity={toast.severity}
      />
    </MainLayout>
  );
};

export default EditAccountDetailsPage;

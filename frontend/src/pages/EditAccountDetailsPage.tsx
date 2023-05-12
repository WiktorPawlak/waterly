import { MainLayout } from "../layouts/MainLayout";
import {
  Box,
  Button,
  CircularProgress,
  Divider,
  Input,
  TextField,
  Typography,
} from "@mui/material";
import loginPose from "../assets/loginPose.svg";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import {
  AccountDto,
  getSelfAccountDetails,
  putAccountDetails,
} from "../api/accountApi";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { editAccountDetilsSchema } from "../validation/validationSchemas";

const EditAccountDetailsPage = () => {
  const [accountDetails, setAccountDetails] = useState<AccountDto>();
  const { t } = useTranslation();
  type FormSchemaType = z.infer<typeof editAccountDetilsSchema>;

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormSchemaType>({
    resolver: zodResolver(editAccountDetilsSchema),
  });

  useEffect(() => {
    getSelfAccountDetails().then((it) => {
      if (it.data) {
        setAccountDetails(it.data);
      } else {
        console.error(it.error);
      }
    });
  }, []);

  const handleEditButton = () => {
    if (accountDetails) {
      putAccountDetails(accountDetails);
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
          <Button
            variant="contained"
            sx={{
              textTransform: "none",
              fontWeight: "700",
              mb: { xs: 5, md: 6 },
            }}
            onClick={handleSubmit(handleEditButton)}
          >
            {t("editAccountDetailsPage.button")}
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
                {t("editAccountDetailsPage.editAccountDetailEntry.emailLabel")}
              </Typography>
            </Box>
            <TextField
              {...register("email")}
              error={errors.email?.message !== undefined}
              helperText={errors.email?.message}
              variant="standard"
              value={accountDetails.email}
              onChange={(e) => {
                setAccountDetails({
                  ...accountDetails,
                  email: e.target.value,
                });
              }}
              sx={{ color: "text.secondary" }}
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
                  "editAccountDetailsPage.editAccountDetailEntry.firstNameLabel"
                )}
              </Typography>
            </Box>
            <TextField
              {...register("firstName")}
              error={errors.firstName?.message !== undefined}
              helperText={errors.firstName?.message}
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
              error={errors.lastName?.message !== undefined}
              helperText={errors.lastName?.message}
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
              error={errors.phoneNumber?.message !== undefined}
              helperText={errors.phoneNumber?.message}
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
    </MainLayout>
  );
};

export default EditAccountDetailsPage;

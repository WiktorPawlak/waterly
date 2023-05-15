import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AccountDto, getUserById } from "../../../api/accountApi";
import { MainLayout } from "../../../layouts/MainLayout";
import { Box, CircularProgress, Typography, Button } from "@mui/material";
import { StyledTextField } from "./AccountDetailsPage.styled";

const AccountDetailsPage = () => {
  const { id } = useParams();
  const [account, setAccount] = useState<AccountDto>();

  useEffect(() => {
    if (id) {
      getUserById(parseInt(id)).then((response) => {
        console.log(response.status);
        if (response.data) {
          setAccount(response.data!);
        } else {
          console.error(response.error);
        }
      });
    }
  }, []);

  if (!account) {
    return <CircularProgress />;
  }

  return (
    <MainLayout isOverflowHidden={false}>
      <Box
        sx={{
          height: "100vh",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          Account Details
        </Typography>
        <Typography sx={{ mb: 3, color: "text.secondary" }}>
          Here you can see user's details.
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
          >
            Dodaj role
          </Button>
          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
          >
            Odbierz role
          </Button>

          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
          >
            Edytuj
          </Button>
          <Button
            sx={{ mr: 2, textTransform: "none", mb: 2 }}
            variant="contained"
          >
            Zmien haslo
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
              label="Login"
              variant="standard"
              value={account.login}
            />
            <StyledTextField
              label="Email"
              variant="standard"
              value={account.email}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="First Name"
              variant="standard"
              value={account.firstName}
            />
            <StyledTextField
              variant="standard"
              label="Last Name"
              value={account.lastName}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="Phone Number"
              variant="standard"
              value={account.phoneNumber}
            />
            <StyledTextField
              label="Language Tag"
              variant="standard"
              value={account.languageTag}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="Roles"
              variant="standard"
              value={account.roles.join(", ")}
            />
            <StyledTextField
              label="Active"
              variant="standard"
              value={account.active ? "Yes" : "No"}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="Created On"
              variant="standard"
              value={account.createdOn}
            />
            <StyledTextField
              label="Created By"
              variant="standard"
              value={account.createdBy}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="Updated On"
              variant="standard"
              value={account.updatedOn}
            />
            <StyledTextField
              label="Updated By"
              variant="standard"
              value={account.updatedBy}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="Last Success Auth"
              variant="standard"
              value={account.lastSuccessAuth}
            />
            <StyledTextField
              label="Last Incorrect Auth"
              variant="standard"
              value={account.lastIncorrectAuth}
              sx={{ mr: { xs: 0, md: 3 } }}
            />
            <StyledTextField
              label="Last IP Address"
              variant="standard"
              value={account.lastIpAddress}
            />
            <StyledTextField
              label="Incorrect Auth Count"
              variant="standard"
              value={account.incorrectAuthCount}
            />
          </Box>
        </Box>
      </Box>
    </MainLayout>
  );
};

export default AccountDetailsPage;

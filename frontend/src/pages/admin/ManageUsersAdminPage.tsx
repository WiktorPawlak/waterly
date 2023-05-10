import { MainLayout } from "../../layouts/MainLayout";
import { Box, Button, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";

export const ManageUsersAdminPage = () => {
  const columns: GridColDef[] = [
    {
      field: "id",
      renderHeader: () => <strong>{"ID"}</strong>,
      width: 70,
    },
    {
      field: "name",
      renderHeader: () => <strong>{"Name"}</strong>,
      width: 330,
    },
    {
      field: "role",
      renderHeader: () => <strong>{"Role"}</strong>,
      width: 130,
    },
    {
      field: "date",
      renderHeader: () => <strong>{"Add date"}</strong>,
      width: 130,
    },
    {
      field: "actions",
      headerName: "",
      description: "This column has a value getter and is not sortable.",
      sortable: false,
      width: 80,
      renderCell: () => {
        return (
          <Button variant="text">
            <LockOutlinedIcon sx={{ color: "red" }} />
          </Button>
        );
      },
    },
  ];

  const rows = [
    { id: 1, role: "Admin", name: "John Snow", date: "20.09.2022" },
    { id: 2, role: "User", name: "Jan Kowalski", date: "01.10.2022" },
    { id: 3, role: "FM", name: "Alina Przykladowa", date: "10.08.2022" },
  ];

  const { t } = useTranslation();
  return (
    <MainLayout>
      <Box
        sx={{
          height: "100vh",
          mx: { xs: 2, md: 4 },
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("manageUsersPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 10, md: 10 }, color: "text.secondary" }}>
          {t("manageUsersPage.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            width: "100%",
          }}
        >
          <Button
            variant="contained"
            sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
          >
            {t("manageUsersPage.button")}
          </Button>
          <Box sx={{ height: 400, width: "750px" }}>
            <DataGrid
              rows={rows}
              columns={columns}
              paginationModel={{ page: 0, pageSize: 10 }}
            />
          </Box>
        </Box>
      </Box>
    </MainLayout>
  );
};

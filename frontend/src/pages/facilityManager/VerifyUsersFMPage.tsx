import { MainLayout } from "../../layouts/MainLayout";
import { Box, Button, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import CheckIcon from "@mui/icons-material/Check";
import ClearIcon from "@mui/icons-material/Clear";

export const VerifyUsersFMPage = () => {
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
      field: "actions",
      headerName: "",
      description: "This column has a value getter and is not sortable.",
      sortable: false,
      width: 150,
      renderCell: () => {
        return (
          <>
            <Button variant="text">
              <CheckIcon sx={{ color: "green" }} />
            </Button>
            <Button variant="text">
              <ClearIcon sx={{ color: "red" }} />
            </Button>
          </>
        );
      },
    },
  ];

  const rows = [
    { id: 1, name: "John Snow" },
    { id: 2, name: "Jan Kowalski" },
    { id: 3, name: "Alina Przykladowa" },
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
          {t("verifyUsersFMPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 10, md: 10 }, color: "text.secondary" }}>
          {t("verifyUsersFMPage.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            width: "100%",
          }}
        >
          <Box sx={{ height: 400, width: "560px" }}>
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

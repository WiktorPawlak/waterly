import {MainLayout} from "../../layouts/MainLayout";
import {useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {DataGrid, GridCellParams, GridColDef, GridColumnHeaderParams, GridSortModel,} from "@mui/x-data-grid";
import {useCallback, useEffect, useState} from "react";
import {getAccountsList, GetPagedAccountListDto, ListAccountDto, PaginatedList,} from "../../api/accountApi";
import {Box, Button, FormControl, InputLabel, MenuItem, Pagination, Select, SelectChangeEvent, Typography,} from "@mui/material";
import {Lock} from "../../layouts/components/account";

export const ManageUsersAdminPage = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [pageState, setPageState] = useState<PaginatedList<ListAccountDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedAccountListDto>({
    page: 1,
    pageSize: 10,
    order: "asc",
    orderBy: "login",
  });

  useEffect(() => {
    getAccountsList(listRequest).then((response) => {
      if (response.status === 200) {
        setPageState(response.data!);
      } else {
        console.error(response.error);
      }
    });
  }, [
    listRequest.page,
    listRequest.pageSize,
    listRequest.order,
    listRequest.orderBy,
  ]);

  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    page: number
  ) => {
    setListRequest((prevListRequest) => ({
      ...prevListRequest,
      page: page,
    }));
  };

  const handleCellClick = (params: GridCellParams) => {
    if (params.field != "actions") {
      const accountId = params.row.id;
      navigate(`/accounts/${accountId}/details`);
    }
  };

  const handlePageSizeChange = (event: SelectChangeEvent) => {
    setListRequest((prevListRequest) => ({
      ...prevListRequest,
      page: 1,
      pageSize: parseInt(event.target.value),
    }));
  };

  const handleOnColumnHeaderClick = (column: GridColumnHeaderParams) => {
    if (
      column.field != "id" &&
      column.field != "roles" &&
      column.field != "actions"
    )
      setListRequest((old) => ({ ...old, orderBy: column.field }));
  };

  const handleSortModelChange = useCallback((sortModel: GridSortModel) => {
    console.log(sortModel);
    setListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
  }, []);

  function translateRoles(t) {
    return (params: GridCellParams) => {
      return (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
          }}
        >
          {params.row.roles.map((role: string) => (
            <Typography key={role} variant="body2">
              {t("roles." + role)}
            </Typography>
          ))}
        </Box>
      );
    };
  }

  const columns: GridColDef[] = [
    {
      field: "id",
      renderHeader: () => <strong>{"ID"}</strong>,
      width: 70,
      filterable: false,
      sortable: false,
      disableColumnMenu: true,
      disableReorder: true,
    },
    {
      field: "login",
      renderHeader: () => (
        <strong>{t("manageUsersPage.dataGrid.header.login")}</strong>
      ),
      width: 120,
    },
    {
      field: "firstName",
      renderHeader: () => (
        <strong>{t("manageUsersPage.dataGrid.header.firstName")}</strong>
      ),
      width: 150,
    },
    {
      field: "lastName",
      renderHeader: () => (
        <strong>{t("manageUsersPage.dataGrid.header.lastName")}</strong>
      ),
      width: 150,
    },
    {
      field: "roles",
      disableColumnMenu: true,
      disableReorder: true,
      filterable: false,
      sortable: false,
      renderHeader: () => (
        <strong>{t("manageUsersPage.dataGrid.header.roles")}</strong>
      ),
      renderCell: translateRoles(t),
      width: 300,
    },
    {
      field: "actions",
      headerName: "",
      description: "This column has a value getter and is not sortable.",
      sortable: false,
      width: 80,
      renderCell: (params) => <Lock accountId={params.row.id} />,
    },
  ];

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
          <Box sx={{ height: 600, width: "100%" }}>
            <DataGrid
              autoHeight
              rows={pageState?.data ?? []}
              columns={columns}
              onColumnHeaderClick={handleOnColumnHeaderClick}
              sortingMode="server"
              onSortModelChange={handleSortModelChange}
              disableColumnMenu={true}
              hideFooterPagination={true}
              onCellClick={handleCellClick}
              sortingOrder={["asc", "desc"]}
            />
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                width: "100%",
              }}
            >
              <Pagination
                count={pageState.totalPages as number}
                page={listRequest.page as number}
                onChange={handlePageChange}
              />
              <FormControl variant="standard" sx={{ m: 1, minWidth: 120 }}>
                <InputLabel id="demo-simple-select-standard-label">
                  {t("manageUsersPage.pageSize")}
                </InputLabel>
                <Select
                  labelId="demo-simple-select-standard-label"
                  id="demo-simple-select-standard"
                  value={listRequest?.pageSize}
                  onChange={handlePageSizeChange}
                  label="pageSize"
                >
                  <MenuItem value={10}>10</MenuItem>
                  <MenuItem value={20}>20</MenuItem>
                  <MenuItem value={30}>30</MenuItem>
                </Select>
              </FormControl>
            </Box>
          </Box>
        </Box>
      </Box>
    </MainLayout>
  );
};
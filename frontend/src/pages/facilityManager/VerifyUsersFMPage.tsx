import { useTranslation } from "react-i18next";
import {
  DataGrid,
  GridColDef,
  GridColumnHeaderParams,
  GridSortModel,
} from "@mui/x-data-grid";
import React, { useCallback, useEffect, useState } from "react";

import {
  Autocomplete,
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  SelectChangeEvent,
  TextField,
  Typography,
} from "@mui/material";
import {
  getNotConfirmedAccoutsList,
  GetPagedListDto,
  getSelfSearchPreferences,
  ListAccountDto,
  PaginatedList,
} from "../../api/accountApi";
import { MainLayout } from "../../layouts/MainLayout";
import { AcceptOrRejectAccount } from "../../layouts/components/account/AcceptOrRejectAccount";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../api/apiErrors";

export const VerifyUsersFMPage = () => {
  const { t } = useTranslation();
  const [fetchSearchPreferencesCompleted, setFetchSearchPreferencesCompleted] =
    useState(false);
  const [pattern, setPattern] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [shouldFetchData, setShouldFetchData] = useState(false);
  const [pageState, setPageState] = useState<PaginatedList<ListAccountDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedListDto>({
    page: 1,
    pageSize: 10,
    order: "asc",
    orderBy: "login",
  });

  useEffect(() => {
    getSelfSearchPreferences().then((response) => {
      if (response.status === 200) {
        setListRequest((prevState) => ({
          ...prevState,
          pageSize: response.data?.pageSize || prevState.pageSize,
          order: response.data?.order || prevState.order,
          orderBy: response.data?.orderBy || prevState.orderBy,
        }));
      } else {
        console.error(response.error);
      }
      setFetchSearchPreferencesCompleted(true);
    });
    fetchData();
  }, []);

  useEffect(() => {
    if (listRequest) {
      fetchData();
    }
  }, [listRequest, pattern, shouldFetchData]);

  const fetchData = () => {
    setIsLoading(true);
    getNotConfirmedAccoutsList(listRequest, pattern).then((response) => {
      if (response.status === 200) {
        setPageState(response.data!);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
      setIsLoading(false);
    });
  };

  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    page: number
  ) => {
    setListRequest((prevListRequest) => ({
      ...prevListRequest,
      page: page,
    }));
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
    setListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
  }, []);

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
      headerName: "",
      field: "accept",
      description: "This column has a value getter and is not sortable.",
      sortable: false,
      width: 80,
      renderCell: (params) => (
        <AcceptOrRejectAccount
          accountId={params.row.id}
          shouldFetchData={shouldFetchData}
          setShouldFetchData={setShouldFetchData}
          accept={true}
        />
      ),
    },
    {
      headerName: "",
      field: "reject",
      description: "This column has a value getter and is not sortable.",
      sortable: false,
      width: 80,
      renderCell: (params) => (
        <AcceptOrRejectAccount
          accountId={params.row.id}
          shouldFetchData={shouldFetchData}
          setShouldFetchData={setShouldFetchData}
          accept={false}
        />
      ),
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
          <Autocomplete
            disablePortal
            freeSolo
            options={[]}
            sx={{ width: "35vw" }}
            onInputChange={(event, newInputValue) => {
              setPattern(newInputValue);
            }}
            renderInput={(params) => (
              <TextField
                label={t("manageUsersPage.searchLabel")}
                sx={{ color: "text.secondary", marginBottom: "15px" }}
                {...params}
              />
            )}
          />
          <Box sx={{ height: 600, width: "75%" }}>
            <DataGrid
              autoHeight
              hideFooter
              loading={isLoading}
              rows={pageState?.data ?? []}
              columns={columns}
              onColumnHeaderClick={handleOnColumnHeaderClick}
              sortingMode="server"
              onSortModelChange={handleSortModelChange}
              disableColumnMenu={true}
              hideFooterPagination={true}
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
                  value={listRequest?.pageSize?.toString()}
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

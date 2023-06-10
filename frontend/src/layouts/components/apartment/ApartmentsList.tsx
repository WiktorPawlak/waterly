import { useTranslation } from "react-i18next";
import {
  DataGrid,
  GridColDef,
  GridColumnHeaderParams,
  GridSortModel,
} from "@mui/x-data-grid";
import React, { useCallback, useEffect, useState } from "react";
import AddIcon from "@mui/icons-material/Add";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import {
  Autocomplete,
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  SelectChangeEvent,
  TextField,
  Typography,
} from "@mui/material";
import { GetPagedAccountListDto, PaginatedList } from "../../../api/accountApi";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../../api/apiErrors";
import { ApartmentDto, getAllAprtmentsList } from "../../../api/apartmentApi";
import { MainLayout } from "../../MainLayout";

export const ApartmentsList = () => {
  const { t } = useTranslation();
  const [pattern, setPattern] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [pageState, setPageState] = useState<PaginatedList<ApartmentDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedAccountListDto>({
    page: 1,
    pageSize: 10,
    order: "asc",
    orderBy: "number",
  });

  useEffect(() => {
    if (listRequest) {
      fetchData();
    }
  }, [listRequest, pattern]);

  const fetchData = () => {
    setIsLoading(true);
    getAllAprtmentsList(listRequest, pattern).then((response) => {
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
      width: 150,
      filterable: false,
      sortable: false,
      disableColumnMenu: true,
      disableReorder: true,
    },
    {
      field: "number",
      renderHeader: () => (
        <strong>{t("apartmentPage.dataGrid.header.number")}</strong>
      ),
      width: 200,
    },
    {
      field: "area",
      renderHeader: () => (
        <strong>{t("apartmentPage.dataGrid.header.area")}</strong>
      ),
      renderCell: (params) => (
        <span>
          {params.value} m<sup>2</sup>
        </span>
      ),
      width: 200,
    },
    {
      headerName: "",
      field: "edit",
      sortable: false,
      hideable: true,
      width: 80,
      align: "right",
      renderCell: (params) => (
        <Button>
          <EditOutlinedIcon />
        </Button>
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
          {t("apartmentPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 10, md: 5 }, color: "text.secondary" }}>
          {t("apartmentPage.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            rowGap: "20px",
            width: "100%",
          }}
        >
          <Button
            variant="contained"
            sx={{ textTransform: "none" }}
            // onClick={() => setCreateAccountByAdminDialogOpen(true)}
          >
            <AddIcon />
            {t("apartmentPage.addButton")}
          </Button>
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

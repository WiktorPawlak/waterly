import { useTranslation } from "react-i18next";
import {
  DataGrid,
  GridColDef,
  GridColumnHeaderParams,
  GridSortModel,
} from "@mui/x-data-grid";
import React, { useCallback, useEffect, useState } from "react";
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
import { PaginatedList } from "../../api/accountApi";
import { MainLayout } from "../../layouts/MainLayout";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../api/apiErrors";
import {
  GetPagedWaterMetersListDto,
  getWaterMetersList,
  ListWaterMeterDto,
} from "../../api/waterMeterApi";
import { WaterMeterLock } from "../../layouts/components/watermeter/WaterMeterLock";

export const WaterMetersListFMPage = () => {
  const { t } = useTranslation();

  const [isLoading, setIsLoading] = useState(false);
  const [pageState, setPageState] = useState<PaginatedList<ListWaterMeterDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedWaterMetersListDto>({
    page: 1,
    order: "asc",
  });

  useEffect(() => {
    if (listRequest) {
      fetchData();
    }
  }, [listRequest]);

  const fetchData = () => {
    setIsLoading(true);
    getWaterMetersList(listRequest).then((response) => {
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

  const handleWaterMeterStatusChange = (
    waterMeterId: number,
    isActive: boolean
  ) => {
    setPageState((prevPageState) => {
      const updatedData = prevPageState.data.map((item) => {
        if (item.id === waterMeterId) {
          return { ...item, active: isActive };
        }
        return item;
      });
      return { ...prevPageState, data: updatedData };
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
    }));
  };

  const handleOnColumnHeaderClick = (column: GridColumnHeaderParams) => {
    if (column.field != "actions")
      setListRequest((old) => ({ ...old, orderBy: column.field }));
  };

  const handleSortModelChange = useCallback((sortModel: GridSortModel) => {
    setListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
  }, []);

  const columns: GridColDef[] = [
    {
      field: "id",
      renderHeader: () => (
        <strong>{t("WaterMetersListFMPage.dataGrid.headers.id")}</strong>
      ),
      width: 70,
    },
    {
      field: "active",
      renderHeader: () => (
        <strong>{t("WaterMetersListFMPage.dataGrid.headers.active")}</strong>
      ),
      width: 70,
    },
    {
      field: "apartmentId",
      renderHeader: () => (
        <strong>
          {t("WaterMetersListFMPage.dataGrid.headers.apartmentId")}
        </strong>
      ),
      width: 150,
    },
    {
      field: "expectedDailyUsage",
      renderHeader: () => (
        <strong>
          {t("WaterMetersListFMPage.dataGrid.headers.expectedDailyUsage")}
        </strong>
      ),
      width: 150,
    },
    {
      field: "expiryDate",
      renderHeader: () => (
        <strong>
          {t("WaterMetersListFMPage.dataGrid.headers.expiryDate")}
        </strong>
      ),
      width: 150,
    },
    {
      field: "startingValue",
      renderHeader: () => (
        <strong>
          {t("WaterMetersListFMPage.dataGrid.headers.startingValue")}
        </strong>
      ),
      width: 150,
    },
    {
      field: "type",
      renderHeader: () => (
        <strong>{t("WaterMetersListFMPage.dataGrid.headers.type")}</strong>
      ),
      width: 120,
    },
    {
      headerName: "",
      field: "accept",
      description: "This column has a value getter and is not sortable.",
      sortable: false,
      width: 80,
      renderCell: (params) => (
        <WaterMeterLock
          waterMeterId={params.row.id}
          active={params.row.active}
          onStatusChange={handleWaterMeterStatusChange}
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
          display: "flex",
          flexDirection: "column",
          height: "100vh",
          mx: 2,
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("WaterMetersListFMPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 10, md: 10 }, color: "text.secondary" }}>
          {t("WaterMetersListFMPage.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            minWidth: "1000px",
          }}
        >
          <Autocomplete
            disablePortal
            freeSolo
            options={[]}
            sx={{ width: "35vw" }}
            renderInput={(params) => (
              <TextField
                label={t("manageUsersPage.searchLabel")}
                sx={{ color: "text.secondary", marginBottom: "15px" }}
                {...params}
              />
            )}
          />
          <Box sx={{ height: 600, width: "100%" }}>
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

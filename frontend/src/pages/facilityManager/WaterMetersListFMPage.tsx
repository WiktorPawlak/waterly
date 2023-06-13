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
import { GetPagedListDto, PaginatedList } from "../../api/accountApi";
import { MainLayout } from "../../layouts/MainLayout";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../api/apiErrors";
import {
  GetPagedWaterMetersListDto,
  getWaterMeterById,
  getWaterMetersList,
  WaterMeterDto,
} from "../../api/waterMeterApi";
import { WaterMeterLock } from "../../layouts/components/watermeter/WaterMeterLock";
import { EditWaterMeterModal } from "../../layouts/components/watermeter/EditWaterMeterModal";
import { roles } from "../../types";
import { useAccount } from "../../hooks/useAccount";
import { AddMainWaterMeterModal } from "../../layouts/components/watermeter/AddMainWaterMeterModal";
import { ApartmentDto, getAllAprtmentsList } from "../../api/apartmentApi";
import { HttpStatusCode } from "axios";

export const WaterMetersListFMPage = () => {
  const { account } = useAccount();
  const { t } = useTranslation();

  const [addMainWaterMeterDialogOpen, setAddMainWaterMeterDialogOpen] = useState(false);
  const [editWaterMeterDialogOpen, setEditWaterMeterDialogOpen] = useState(false);
  const [selectedWaterMeter, setSelectedWaterMeter] = useState<WaterMeterDto>();
  const [selectedWaterMeterEtag, setSelectedWaterMeterEtag] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [waterMetersPageState, setWaterMetersPageState] = useState<PaginatedList<WaterMeterDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [waterMetersListRequest, setWaterMetersListRequest] = useState<GetPagedWaterMetersListDto>({
    page: 1,
    order: "asc",
  });

  const [apartmentsPageState, setApartmentsPageState] = useState<PaginatedList<ApartmentDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [apartmentsListRequest, setApartmentsListRequest] = useState<GetPagedListDto>({
    page: 1,
    pageSize: 100,
    order: "asc",
    orderBy: "number",
  });

  useEffect(() => {
    if (waterMetersListRequest) {
      fetchData();
    }
  }, [waterMetersListRequest, editWaterMeterDialogOpen, addMainWaterMeterDialogOpen]);

  const fetchData = () => {
    setIsLoading(true);
    getWaterMetersList(waterMetersListRequest).then((response) => {
      if (response.status === HttpStatusCode.Ok) {
        setWaterMetersPageState(response.data!);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
    });
    getAllAprtmentsList(apartmentsListRequest, '').then((response) => {
      if (response.status === HttpStatusCode.Ok) {
        setApartmentsPageState(response.data!);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
    });
    setIsLoading(false);
  };

  const handleWaterMeterStatusChange = (
    waterMeterId: number,
    isActive: boolean
  ) => {
    setWaterMetersPageState((prevPageState) => {
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
    setWaterMetersListRequest((prevListRequest) => ({
      ...prevListRequest,
      page: page,
    }));
  };

  const handlePageSizeChange = (event: SelectChangeEvent) => {
    setWaterMetersListRequest((prevListRequest) => ({
      ...prevListRequest,
      page: 1,
    }));
  };

  const handleCellClick = async (id: number) => {
    if (account?.currentRole === roles.facilityManager) {
        const response = await getWaterMeterById(id);
        if (response.data) {
            setSelectedWaterMeter(response.data!);
            setSelectedWaterMeterEtag(response.headers!["etag"] as string);
        } else {
            enqueueSnackbar(t(resolveApiError(response.error)), {
                variant: "error",
            });
        }
        setEditWaterMeterDialogOpen(true);
    }
};

  const handleOnColumnHeaderClick = (column: GridColumnHeaderParams) => {
    if (column.field != "actions")
      setWaterMetersListRequest((old) => ({ ...old, orderBy: column.field }));
  };

  const handleSortModelChange = useCallback((sortModel: GridSortModel) => {
    setWaterMetersListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
  }, []);

  const columns: GridColDef[] = [
    {
      field: "id",
      renderHeader: () => (
        <strong>{t("WaterMetersListFMPage.dataGrid.headers.id")}</strong>
      ),
      width: 50,
    },
    {
      field: "apartmentId",
      renderHeader: () => (
        <strong>
          {t("WaterMetersListFMPage.dataGrid.headers.apartmentNo")}
        </strong>
      ),
      renderCell: (params) => (
        <span>
          {apartmentsPageState.data.find((obj) => obj.id === params.row.apartmentId)?.number}
        </span>
      ),
      width: 120,
    },
    {
      field: "expectedDailyUsage",
      renderHeader: () => (
        <strong>
          {t("WaterMetersListFMPage.dataGrid.headers.expectedDailyUsage")}
        </strong>
      ),
      renderCell: (params) => (
        <span>
          {params.row.type === 'MAIN' ? '' : params.row.expectedDailyUsage}
        </span>
      ),
      width: 180,
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
      renderCell: (params) => (
        <span>
          {params.row.type === 'MAIN' ? (
            <span>
              {t("WaterMetersListFMPage.dataGrid.cells.type.main")}
            </span>
          ) : params.row.type === 'COLD_WATER' ? (
            <span>
              {t("WaterMetersListFMPage.dataGrid.cells.type.coldWater")}
            </span>
          ) : params.row.type === 'HOT_WATER' ? (
            <span>
              {t("WaterMetersListFMPage.dataGrid.cells.type.hotWater")}
            </span>
          ) : null}
        </span>
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
        <Button onClick={() => handleCellClick(params.row.id)}>
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
        <AddMainWaterMeterModal
          isOpen={addMainWaterMeterDialogOpen}
          setIsOpen={setAddMainWaterMeterDialogOpen}
        />
        <EditWaterMeterModal
          isOpen={editWaterMeterDialogOpen}
          setIsOpen={setEditWaterMeterDialogOpen}
          waterMeter={selectedWaterMeter}
          apartments={apartmentsPageState}
          etag={selectedWaterMeterEtag}
        />
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
        {account?.currentRole === roles.facilityManager &&
          <Button
            disabled={!!waterMetersPageState.data.find((obj) => (obj.type === 'MAIN' && obj.active))}
            variant="contained"
            sx={{
                textTransform: "none", mb: { xs: 3, md: 2 },
                width: "30vh"
            }}
            onClick={() => setAddMainWaterMeterDialogOpen(true)}
          >
              {t("addMainWaterMeterDialog.button")}
          </Button>}
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
              rows={waterMetersPageState?.data ?? []}
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
                count={waterMetersPageState.totalPages as number}
                page={waterMetersListRequest.page as number}
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

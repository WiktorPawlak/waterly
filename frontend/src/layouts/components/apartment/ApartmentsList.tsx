import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import {
  DataGrid,
  GridCellParams,
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
import { GetPagedListDto, PaginatedList } from "../../../api/accountApi";
import { enqueueSnackbar } from "notistack";
import { resolveApiError } from "../../../api/apiErrors";
import {
  ApartmentDto,
  getAllAprtmentsList,
  getApartmentById,
} from "../../../api/apartmentApi";
import { MainLayout } from "../../MainLayout";
import { CreateApartmentDialog } from "./CreateApartmentDialog";
import { EditApartmentUserModal } from "./EditApartmentUserModal";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import { EditApartmentDialog } from "./EditApartmentDialog";

export const ApartmentsList = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [pattern, setPattern] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [addApartmentDialogIsOpen, setAddApartmentDialogIsOpen] =
    useState(false);
  const [editApartmentDialogIsOpen, setEditApartmentDialogIsOpen] =
    useState(false);
  const [pageState, setPageState] = useState<PaginatedList<ApartmentDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedListDto>({
    page: 1,
    pageSize: 10,
    order: "asc",
    orderBy: "id",
  });
  const [selectedApartment, setSelectedApartment] = useState<ApartmentDto>();
  const [selectedApartmentEtag, setSelectedApartmentEtag] = useState("");
  const [editApartmentOwnerModalOpen, setEditApartmentOwnerModalOpen] =
    useState(false);
  useEffect(() => {
    if (listRequest) {
      fetchData();
    }
  }, [
    listRequest,
    pattern,
    addApartmentDialogIsOpen,
    editApartmentOwnerModalOpen,
    editApartmentDialogIsOpen,
  ]);

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
      column.field === "id" ||
      column.field === "number" ||
      column.field === "area"
    ) {
      setListRequest((old) => ({ ...old, orderBy: column.field }));
    }
  };

  const handleSortModelChange = useCallback((sortModel: GridSortModel) => {
    setListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
  }, []);

  const getApartment = async (id: number) => {
    const response = await getApartmentById(id);
    if (response.data) {
      setSelectedApartment(response.data!);
      setSelectedApartmentEtag(response.headers!["etag"] as string);
    } else {
      enqueueSnackbar(t(resolveApiError(response.error)), {
        variant: "error",
      });
    }
  };

  const handleCellClick = async (params: GridCellParams) => {
    if (params.field === "changeUser") {
      await getApartment(params.row.id);
      setEditApartmentOwnerModalOpen(true);
    } else if (params.field === "edit") {
      await getApartment(params.row.id);
      setEditApartmentDialogIsOpen(true);
    } else {
      const apartmentId = params.row.id;
      navigate(`/apartments/${apartmentId}`);
    }
  };

  const columns: GridColDef[] = [
    {
      field: "id",
      renderHeader: () => <strong>{"ID"}</strong>,
      width: 150,
      filterable: false,
      sortable: true,
      disableColumnMenu: true,
      disableReorder: true,
    },
    {
      field: "number",
      renderHeader: () => (
        <strong>{t("apartmentPage.dataGrid.header.number")}</strong>
      ),
      width: 130,
    },
    {
      field: "area",
      renderHeader: () => (
        <strong>{t("apartmentPage.dataGrid.header.area")}</strong>
      ),
      renderCell: (params) => (
        <span>
          {params.value.toFixed(2)} m<sup>2</sup>
        </span>
      ),
      width: 130,
    },
    {
      field: "ownerName",
      sortable: false,
      renderHeader: () => (
        <strong>{t("apartmentDetailsPage.ownerTitle")}</strong>
      ),
      width: 250,
    },
    {
      headerName: "",
      field: "changeUser",
      sortable: false,
      hideable: true,
      width: 80,
      align: "right",
      renderCell: (_) => (
        <>
          <Button>
            <ManageAccountsIcon />
          </Button>
        </>
      ),
    },
    {
      headerName: "",
      field: "edit",
      hideable: true,
      sortable: false,
      width: 80,
      align: "right",
      renderCell: (_) => (
        <>
          <Button>
            <EditOutlinedIcon />
          </Button>
        </>
      ),
    },
  ];

  return (
    <MainLayout>
      <CreateApartmentDialog
        isOpen={addApartmentDialogIsOpen}
        setIsOpen={setAddApartmentDialogIsOpen}
      />
      <EditApartmentUserModal
        isOpen={editApartmentOwnerModalOpen}
        setIsOpen={setEditApartmentOwnerModalOpen}
        apartment={selectedApartment}
        etag={selectedApartmentEtag}
      />
      <EditApartmentDialog
        apartment={selectedApartment}
        isOpen={editApartmentDialogIsOpen}
        setIsOpen={setEditApartmentDialogIsOpen}
      />
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
            onClick={() => setAddApartmentDialogIsOpen(true)}
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

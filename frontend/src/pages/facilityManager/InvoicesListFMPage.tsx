import { Trans, useTranslation } from "react-i18next";
import {
  DataGrid,
  GridCellParams,
  GridColDef,
  GridColumnHeaderParams,
  GridSortModel,
} from "@mui/x-data-grid";
import React, { useCallback, useEffect, useState } from "react";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import LocalPrintshopOutlinedIcon from "@mui/icons-material/LocalPrintshopOutlined";

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
  getInvoiceById,
  getInvoicesList,
  GetPagedInvoicesListDto,
  InvoiceDto,
} from "../../api/invoiceApi";
import { useAccount } from "../../hooks/useAccount";
import { roles } from "../../types";
import { EditInvoiceModal } from "../../layouts/components/Invoice/EditInvoiceModal";
import { AddInvoiceDialog } from "../../layouts/components/Invoice/AddInvoiceModal";

export const InvoicesListFMPage = () => {
  const { t } = useTranslation();

  const { account } = useAccount();
  const [isLoading, setIsLoading] = useState(false);
  const [editInvoiceModalOpen, setEditInvoiceModalOpen] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState<InvoiceDto>();
  const [selectedInvoiceEtag, setSelectedInvoiceEtag] = useState("");
  const [addInvoiceDialogOpen, setAddInvoiceDialogOpen] = useState(false);
  const [pageState, setPageState] = useState<PaginatedList<InvoiceDto>>({
    data: [],
    pageNumber: 1,
    itemsInPage: 0,
    totalPages: 0,
  });

  const [listRequest, setListRequest] = useState<GetPagedInvoicesListDto>({
    page: 1,
    order: "asc",
    pageSize: 10,
    orderBy: "invoiceNumber",
  });

  useEffect(() => {
    if (listRequest) {
      fetchData();
    }
  }, [listRequest, editInvoiceModalOpen, addInvoiceDialogOpen]);

  const fetchData = () => {
    setIsLoading(true);
    getInvoicesList(listRequest).then((response) => {
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

  const handleCellClick = async (params: GridCellParams) => {
    if (account?.currentRole === roles.facilityManager) {
      const response = await getInvoiceById(params.row.id);
      if (response.data) {
        setSelectedInvoice(response.data!);
        setSelectedInvoiceEtag(response.headers!["etag"] as string);
      } else {
        enqueueSnackbar(t(resolveApiError(response.error)), {
          variant: "error",
        });
      }
      setEditInvoiceModalOpen(true);
    }
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
    if (column.field != "actions")
      setListRequest((old) => ({ ...old, orderBy: column.field }));
  };

  const handleSortModelChange = useCallback((sortModel: GridSortModel) => {
    setListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
  }, []);

  const columns: GridColDef[] = [
    {
      field: "invoiceNumber",
      renderHeader: () => (
        <strong>
          {t("InvoicesListFMPage.dataGrid.headers.invoiceNumber")}
        </strong>
      ),
      width: 220,
    },
    {
      field: "date",
      renderHeader: () => (
        <strong>{t("InvoicesListFMPage.dataGrid.headers.date")}</strong>
      ),
      width: 150,
    },
    {
      field: "totalCost",
      renderHeader: () => (
        <strong>{t("InvoicesListFMPage.dataGrid.headers.totalCost")}</strong>
      ),
      width: 150,
    },
    {
      field: "waterUsage",
      renderHeader: () => (
        <strong>
          <Trans
            i18nKey={"InvoicesListFMPage.dataGrid.headers.waterUsage"}
            components={{ sup: <sup /> }}
          />
        </strong>
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
        <Button>
          <EditOutlinedIcon />
        </Button>
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
          <LocalPrintshopOutlinedIcon />
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
        <EditInvoiceModal
          isOpen={editInvoiceModalOpen}
          setIsOpen={setEditInvoiceModalOpen}
          invoice={selectedInvoice}
          etag={selectedInvoiceEtag}
        />
        <AddInvoiceDialog
          isOpen={addInvoiceDialogOpen}
          setIsOpen={setAddInvoiceDialogOpen}
        />
        <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
          {t("InvoicesListFMPage.header")}
        </Typography>
        <Typography sx={{ mb: { xs: 10, md: 10 }, color: "text.secondary" }}>
          {t("InvoicesListFMPage.description")}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            width: "100%",
          }}
        >
          {account?.currentRole === roles.facilityManager && (
            <Button
              variant="contained"
              sx={{
                textTransform: "none",
                mb: { xs: 3, md: 2 },
                width: "30vh",
              }}
              onClick={() => setAddInvoiceDialogOpen(true)}
            >
              {t("InvoicesListFMPage.button")}
            </Button>
          )}
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

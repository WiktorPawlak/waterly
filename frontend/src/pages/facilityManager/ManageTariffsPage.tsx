import { useNavigate } from "react-router-dom";
import { Trans, useTranslation } from "react-i18next";
import {
    DataGrid,
    GridCellParams,
    GridColDef,
    GridColumnHeaderParams,
    GridEventListener,
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
import { enqueueSnackbar } from "notistack";
import { GetPagedTariffsListDto, PaginatedList, TariffDto, getTariffById, getTariffsList } from "../../api/tariffsApi";
import { MainLayout } from "../../layouts/MainLayout";
import { resolveApiError } from "../../api/apiErrors";
import { useAccount } from "../../hooks/useAccount";
import { roles } from "../../types";
import { EditTariffDialog } from "../../layouts/components/tariff/EditTariffModal";

export const ManageTariffsPage = () => {
    const { account } = useAccount();
    const { t } = useTranslation();
    const [editTariffDialogOpen, setEditTariffDialogOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [selectedTariff, setSelectedTariff] = useState<TariffDto>();
    const [selectedTariffEtag, setSelectedTariffEtag] = useState("");
    const [pageState, setPageState] = useState<PaginatedList<TariffDto>>({
        data: [],
        pageNumber: 1,
        itemsInPage: 0,
        totalPages: 0,
    });

    const [listRequest, setListRequest] = useState<GetPagedTariffsListDto>({
        page: 1,
        pageSize: 10,
        order: "asc",
        orderBy: "startDate",
    });

    useEffect(() => {
        if (listRequest) {
            fetchData();
        }
    }, [listRequest, editTariffDialogOpen]);

    const fetchData = () => {
        setIsLoading(true);
        getTariffsList(listRequest).then((response) => {
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

    const handleCellClick = async (params: GridCellParams) => {
        if (account?.currentRole === roles.facilityManager) {
            const response = await getTariffById(params.row.id);
            if (response.data) {
                setSelectedTariff(response.data!);
                setSelectedTariffEtag(response.headers!["etag"] as string);
            } else {
                enqueueSnackbar(t(resolveApiError(response.error)), {
                    variant: "error",
                });
            }
            setEditTariffDialogOpen(true);
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
        setListRequest((old) => ({ ...old, orderBy: column.field }));
    };

    const handleSortModelChange = useCallback((sortModel: GridSortModel) => {
        setListRequest((old) => ({ ...old, order: sortModel[0]?.sort as string }));
    }, []);

    const columns: GridColDef[] = [
        {
            field: "coldWaterPrice",
            renderHeader: () => (
                <strong>
                    <Trans i18nKey={"manageTariffsPage.dataGrid.header.coldWaterPrice"} components={{ sup: <sup /> }} />
                </strong>
            ),
            width: 200,
        },
        {
            field: "hotWaterPrice",
            renderHeader: () => (
                <strong>
                    <Trans i18nKey={"manageTariffsPage.dataGrid.header.hotWaterPrice"} components={{ sup: <sup /> }} />
                </strong>
            ),
            width: 170,
        },
        {
            field: "trashPrice",
            renderHeader: () => (
                <strong>
                    <Trans i18nKey={"manageTariffsPage.dataGrid.header.trashPrice"} components={{ sup: <sup /> }} />
                </strong>
            ),
            width: 170,
        },
        {
            field: "startDate",
            renderHeader: () => (
                <strong>{t("manageTariffsPage.dataGrid.header.startDate")}</strong>
            ),
            width: 170,
        },
        {
            field: "endDate",
            renderHeader: () => (
                <strong>{t("manageTariffsPage.dataGrid.header.endDate")}</strong>
            ),
            width: 170,
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
    ];

    return (
        <MainLayout>
            <Box
                sx={{
                    height: "100vh",
                    mx: { xs: 2, md: 4 },
                }}
            >
                <EditTariffDialog
                    isOpen={editTariffDialogOpen}
                    setIsOpen={setEditTariffDialogOpen}
                    tariff={selectedTariff}
                    etag={selectedTariffEtag}
                />
                <Typography variant="h4" sx={{ fontWeight: "700", mb: 2 }}>
                    {t("manageTariffsPage.header")}
                </Typography>
                <Typography sx={{ mb: { xs: 10, md: 10 }, color: "text.secondary" }}>
                    {t("manageTariffsPage.description")}
                </Typography>
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        width: "100%",
                    }}
                >
                    {account?.currentRole === roles.facilityManager &&
                        <Button
                            variant="contained"
                            sx={{ textTransform: "none", mb: { xs: 3, md: 6 } }}
                            onClick={() => setIsLoading(true)}
                        >
                            {t("manageTariffsPage.button")}
                        </Button>}

                    <Box sx={{ height: 600, width: "100%" }}>
                        <DataGrid
                            autoHeight
                            hideFooter
                            rowHeight={65}
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
                                    {t("manageTariffsPage.pageSize")}
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

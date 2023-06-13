import {
    Box,
    Button,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@mui/material";
import { enqueueSnackbar } from "notistack";
import { useEffect, useMemo, useState } from "react";
import { Trans, useTranslation } from "react-i18next";
import { resolveApiError } from "../../../api/apiErrors";
import { GetBillsByOwnerIdDto, getBillsByOwnerId } from "../../../api/billApi";
import { DatePicker, LocalizationProvider, plPL } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

interface Props {
    apartmentId: number
}

const initialBillData: GetBillsByOwnerIdDto = {
    billId: 0,
    balance: 0,
    apartmentId: 0,
    forecast: {
        garbageCost: 0,
        coldWaterCost: 0,
        hotWaterCost: 0,
        coldWaterUsage: 0,
        hotWaterUsage: 0,
        totalCost: 0
    },
    realUsage: {
        garbageCost: 0,
        garbageBalance: 0,
        coldWaterCost: 0,
        coldWaterBalance: 0,
        hotWaterCost: 0,
        hotWaterbalance: 0,
        coldWaterUsage: 0,
        hotWaterUsage: 0,
        unbilledWaterCost: 0,
        unbilledWaterAmount: 0,
        totalCost: 0
    },
};

export const ShowBillModal = ({
    apartmentId
}: Props) => {
    const { t } = useTranslation();
    const [isLoading, setIsLoading] = useState(false);
    const [billData, setBillData] = useState(initialBillData);
    const [date, setDate] = useState<Dayjs | null>(dayjs());
    const [found, setFound] = useState<boolean>(true);

    useEffect(() => {
        setFound(false);
    }, [apartmentId]);

    const fetchData = async () => {
        setIsLoading(true);
        const response = await getBillsByOwnerId(date!.format('YYYY-MM'), apartmentId);
        console.log(response);
        if (response.status !== 200) {
            enqueueSnackbar(t(resolveApiError(response.error)), {
                variant: "error",
            });
            setFound(false);
        } else {
            setBillData(response.data as GetBillsByOwnerIdDto);
            setFound(true);
        }
        setIsLoading(false);
    };

    const handleClick = (() => {
        fetchData();
    });


    const headers = [
        { label: t("bill.advancedUsage") },
        { label: t("bill.realUsage") },
        { label: t("bill.advancedCost") },
        { label: t("bill.realCost") },
        { label: t("bill.rowBalance") }
    ];

    const tableData = useMemo(() => [
        {
            id: 0,
            name: <Trans
                i18nKey={"bill.coldWater"}
                components={{ sup: <sup /> }}
            />,
            advancedUsage: billData.forecast.coldWaterUsage,
            realUsage: billData.realUsage.coldWaterUsage,
            advancedCost: billData.forecast.coldWaterCost,
            realCost: billData.realUsage.coldWaterCost,
            balance: billData.realUsage.coldWaterBalance
        },
        {
            id: 1,
            name: <Trans
                i18nKey={"bill.hotWater"}
                components={{ sup: <sup /> }}
            />,
            advancedUsage: billData.forecast.hotWaterUsage,
            realUsage: billData.realUsage.hotWaterUsage,
            advancedCost: billData.forecast.hotWaterCost,
            realCost: billData.realUsage.hotWaterCost,
            balance: billData.realUsage.hotWaterbalance
        },
        {
            id: 2,
            name: <Trans
                i18nKey={"bill.trash"}
                components={{ sup: <sup /> }}
            />,
            advancedUsage: "-",
            realUsage: "-",
            advancedCost: billData.forecast.garbageCost,
            realCost: billData.realUsage.garbageCost,
            balance: billData.realUsage.garbageBalance
        },
        {
            id: 3,
            name: <Trans
                i18nKey={"bill.unbilledWater"}
                components={{ sup: <sup /> }}
            />,
            advancedUsage: "-",
            realUsage: billData.realUsage.unbilledWaterAmount,
            advancedCost: "-",
            realCost: billData.realUsage.unbilledWaterCost,
            balance: "-",
        },
        {
            id: 4,
            name: t("bill.summary"),
            balance: billData.balance,
            advancedCost: billData.forecast.totalCost,
            realCost: billData.realUsage.totalCost
        }
    ], [billData]);

    return (
        <Box>
            <LocalizationProvider adapterLocale="pl"
                dateAdapter={AdapterDayjs}
                localeText={plPL.components.MuiLocalizationProvider.defaultProps.localeText}>
                <DatePicker
                    format="YYYY-MM"
                    value={date}
                    onChange={(newValue) => setDate(newValue)}
                    views={['month', 'year']}
                    sx={{
                        mx: '20px',
                        mt: '20px',
                    }}
                />
            </LocalizationProvider>
            <Button
                variant="contained"
                sx={{
                    textTransform: "none",
                    fontWeight: "700",
                    width: "fit-content",
                    marginLeft: '20px',
                    mt: '30px'
                }}
                onClick={() => handleClick()}
            >
                {t("apartmentCard.checkButton")}
            </Button>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>
                        </TableCell>
                        {/* Empty cell for the top-left corner */}
                        {headers.map((header) => (
                            <TableCell key={header.label}>{header.label}
                            </TableCell>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {tableData.map((row) => (
                        <TableRow key={row.id}>
                            <TableCell component="th" scope="row">
                                {row.name}
                            </TableCell>
                            <TableCell>{found ? row.advancedUsage : '-'}</TableCell>
                            <TableCell>{found ? row.realUsage : '-'}</TableCell>
                            <TableCell>{found ? row.advancedCost : '-'}</TableCell>
                            <TableCell>{found ? row.realCost : '-'}</TableCell>
                            <TableCell>{found ? row.balance : '-'}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </Box>
    )
};

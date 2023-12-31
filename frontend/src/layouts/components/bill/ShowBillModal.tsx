import {
  Box,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { enqueueSnackbar } from "notistack";
import { useEffect, useMemo, useState } from "react";
import { Trans, useTranslation } from "react-i18next";
import { resolveApiError } from "../../../api/apiErrors";
import { GetBillsByOwnerIdDto, getBillsDetail, getBillsDetailByOwner } from "../../../api/billApi";
import { monthName } from "../../../common/dates";
import dayjs from "dayjs";
import { roles } from "../../../types/rolesEnum";
import { useAccount } from "../../../hooks/useAccount";

interface Props {
  apartmentId: number;
  yearMonthDate?: Date;
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
    totalCost: 0,
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
    totalCost: 0,
  },
};

export const ShowBillModal = ({ apartmentId, yearMonthDate }: Props) => {
  const { t } = useTranslation();
  const [isLoading, setIsLoading] = useState(false);
  const [billData, setBillData] = useState(initialBillData);
  const { account } = useAccount();
  const [found, setFound] = useState<boolean>(true);
  const managerAuthored = account?.currentRole === roles.facilityManager;

  useEffect(() => {
    setFound(false);
  }, [apartmentId]);

  const fetchData = async () => {
    setIsLoading(true);
    const response = await getBillsDetail
      (
        dayjs(yearMonthDate!!).format("YYYY-MM"),
        apartmentId
      );
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

  const fetchDataByOwner = async () => {
    setIsLoading(true);
    const response = await getBillsDetailByOwner
      (
        dayjs(yearMonthDate!!).format("YYYY-MM"),
        apartmentId
      );
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

  useEffect(() => {
    if (managerAuthored) {
      fetchData();
    } else {
      fetchDataByOwner();
    }
  }, [yearMonthDate]);

  const headers = [
    { id: 0,
      label: (
      <Trans i18nKey={"bill.advancedUsage"} components={{ sup: <sup /> }} />
    )},
    { id: 1,
      label: (
      <Trans i18nKey={"bill.realUsage"} components={{ sup: <sup /> }} />
    )},
    { id: 2, label: t("bill.advancedCost") },
    { id: 3, label: t("bill.realCost") },
    { id: 4, label: t("bill.rowBalance") },
  ];

  const tableData = useMemo(
    () => [
      {
        id: 0,
        name: t("bill.coldWater"),
        advancedUsage: billData.forecast.coldWaterUsage,
        realUsage: billData.realUsage.coldWaterUsage,
        advancedCost: billData.forecast.coldWaterCost,
        realCost: billData.realUsage.coldWaterCost,
        balance: billData.realUsage.coldWaterBalance,
      },
      {
        id: 1,
        name: t("bill.hotWater"),
        advancedUsage: billData.forecast.hotWaterUsage,
        realUsage: billData.realUsage.hotWaterUsage,
        advancedCost: billData.forecast.hotWaterCost,
        realCost: billData.realUsage.hotWaterCost,
        balance: billData.realUsage.hotWaterbalance,
      },
      {
        id: 2,
        name: t("bill.trash"),
        advancedUsage: "-",
        realUsage: "-",
        advancedCost: billData.forecast.garbageCost,
        realCost: billData.realUsage.garbageCost,
        balance: billData.realUsage.garbageBalance,
      },
      {
        id: 3,
        name: t("bill.unbilledWater"),
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
        realCost: billData.realUsage.totalCost,
      },
    ],
    [billData, t]
  );

  if (isLoading && !found) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          textAlign: "center",
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ mt: 3, ml: 3 }}>
        <Typography sx={{ fontSize: "35px" }}>
          {t(monthName(yearMonthDate?.getMonth()!!))}{" "}
          {yearMonthDate?.getFullYear()}
        </Typography>
      </Box>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell></TableCell>
            {/* Empty cell for the top-left corner */}
            {headers.map((header) => (
              <TableCell key={header.id}>{header.label}</TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {tableData.map((row) => (
            <TableRow key={row.id}>
              <TableCell component="th" scope="row">
                {row.name}
              </TableCell>
              <TableCell>{row.advancedUsage}</TableCell>
              <TableCell>{row.realUsage}</TableCell>
              <TableCell>{row.advancedCost}</TableCell>
              <TableCell>{row.realCost}</TableCell>
              <TableCell>{row.balance}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Box>
  );
};

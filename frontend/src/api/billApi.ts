import { ApiResponse, get } from "./api";
const BILLS_PATH = "/bills";

export interface GetBillsByOwnerIdDto {
  billId: number;
  balance: number;
  apartmentId: number;
  forecast: AdvancedUsageReportDto;
  realUsage: RealUsageReportDto;
}

interface AdvancedUsageReportDto {
  garbageCost: number;
  coldWaterCost: number;
  hotWaterCost: number;
  coldWaterUsage: number;
  hotWaterUsage: number;
  totalCost: number;
}

interface RealUsageReportDto {
  garbageCost: number;
  garbageBalance: number;
  coldWaterCost: number;
  coldWaterBalance: number;
  hotWaterCost: number;
  hotWaterbalance: number;
  coldWaterUsage: number;
  hotWaterUsage: number;
  unbilledWaterCost: number;
  unbilledWaterAmount: number;
  totalCost: number;
}

export interface ApartmentBillDto {
  billId: number;
  billDate: string;
  balance: number;
}

export interface OwnerBillDto {
  billId: number;
  billDate: string;
  balance: number;
  apartmentId: number;
  apartmentNumber: string;
}

export async function getBillsDetail(
  date: string,
  apartmentId: number,
): Promise<ApiResponse<GetBillsByOwnerIdDto>> {
  return get(
    `${BILLS_PATH}/facility-manager?date=` + date + `&apartmentId=` + apartmentId
  );
}

export async function getBillsDetailByOwner(
  date: string,
  apartmentId: number,
): Promise<ApiResponse<GetBillsByOwnerIdDto>> {
  return get(
    `${BILLS_PATH}/owner?date=` + date + `&apartmentId=` + apartmentId
  );
}

export async function getBillsByApartmentId(
  apartmentId: number
): Promise<ApiResponse<ApartmentBillDto[]>> {
  return get(`${BILLS_PATH}/apartment/${apartmentId}`);
}

export async function getOwnerBills(
  ownerLogin: string
): Promise<ApiResponse<OwnerBillDto[]>> {
  return get(`${BILLS_PATH}/owner/${ownerLogin}`);
}

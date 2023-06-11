import { ApiResponse, get } from "./api";
const BILLS_PATH = "/bills";

export interface GetBillsByOwnerIdDto {
    billId: number,
    balance: number,
    apartmentId: number,
    forecast: AdvancedUsageReportDto,
    realUsage: RealUsageReportDto
}

interface AdvancedUsageReportDto {
    garbageCost: number,
    coldWaterCost: number,
    hotWaterCost: number,
    coldWaterUsage: number,
    hotWaterUsage: number
}

interface RealUsageReportDto {
    garbageCost: number,
    garbageBalance: number,
    coldWaterCost: number,
    coldWaterBalance: number,
    hotWaterCost: number,
    hotWaterbalance: number,
    coldWaterUsage: number,
    hotWaterUsage: number,
    unbilledWaterCost: number,
    unbilledWaterAmount: number,
}

export async function getBillsByOwnerId(date: string, apartmentId: number
): Promise<ApiResponse<GetBillsByOwnerIdDto>> {
    return get(`${BILLS_PATH}/owner?date=` + date + `&apartmentId=` + apartmentId);
}
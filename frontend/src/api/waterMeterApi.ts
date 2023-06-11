import { ApiResponse, post, put } from "./api";

const WATERMETERS_PATH = "/water-meters";

export interface GetPagedWaterMetersListDto {
  order: string;
  page: number;
}

export interface PaginatedList<T> {
  data: T[];
  pageNumber: number;
  itemsInPage: number;
  totalPages: number;
}

export interface ListWaterMeterDto {
  id: number;
  active: boolean;
  expiryDate: Date;
  expectedDailyUsage: number;
  startingValue: number;
  type: string;
  apartmentId: number;
}

export interface WaterMeterActiveStatusDto {
  active: boolean;
}

export async function getWaterMetersList(
  getPagedListDto: GetPagedWaterMetersListDto
): Promise<ApiResponse<PaginatedList<ListWaterMeterDto>>> {
  return post(`${WATERMETERS_PATH}/list`, getPagedListDto);
}

export async function changeWaterMeterActiveStatus(
  waterMeterId: string,
  body: WaterMeterActiveStatusDto
) {
  return put(`${WATERMETERS_PATH}/${waterMeterId}/active`, body);
}

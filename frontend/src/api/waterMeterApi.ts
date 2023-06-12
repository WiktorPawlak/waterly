import { ApiResponse, get, post, put } from "./api";

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

export interface WaterMeterDto {
  id: number;
  active: boolean;
  expiryDate: Date;
  expectedDailyUsage: number;
  startingValue: number;
  type: string;
  apartmentId: number;
  version: number;
}

export interface WaterMeterActiveStatusDto {
  active: boolean;
}

export async function getWaterMeterById(
  id: number,
): Promise<ApiResponse<WaterMeterDto>> {
  return get(`${WATERMETERS_PATH}/${id}`);
}

export async function getWaterMetersList(
  getPagedListDto: GetPagedWaterMetersListDto
): Promise<ApiResponse<PaginatedList<WaterMeterDto>>> {
  return post(`${WATERMETERS_PATH}/list`, getPagedListDto);
}

export async function changeWaterMeterActiveStatus(
  waterMeterId: string,
  body: WaterMeterActiveStatusDto
) {
  return put(`${WATERMETERS_PATH}/${waterMeterId}/active`, body);
}

export async function updateWaterMeter(id: number, updatedWaterMeter: WaterMeterDto, etag: string) {
  return put(`${WATERMETERS_PATH}/${id}`, updatedWaterMeter,{
      "If-Match": etag,
  });
}

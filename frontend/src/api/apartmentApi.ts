import { GetPagedAccountListDto, PaginatedList } from "./accountApi";
import { ApiResponse, get, post } from "./api";

const APARTMENTS_PATH = "/apartments";

export interface ApartmentDto {
  id: number;
  number: string;
  area: number;
  ownerId: number;
}

export interface CreateApartmentDto {
  number: string;
  area: number;
  ownerId: number;
}

export async function getAllAprtmentsList(
  getPagedListDto: GetPagedAccountListDto,
  pattern: string
): Promise<ApiResponse<PaginatedList<ApartmentDto>>> {
  return get(`${APARTMENTS_PATH}`, { ...getPagedListDto, pattern: pattern });
}

export async function createAprtment(
  body: CreateApartmentDto
): Promise<ApiResponse<PaginatedList<ApartmentDto>>> {
  return post(`${APARTMENTS_PATH}`, body);
}

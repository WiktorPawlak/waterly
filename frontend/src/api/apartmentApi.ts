import { GetPagedListDto, List, PaginatedList } from "./accountApi";
import { ApiResponse, get, post } from "./api";

const APARTMENTS_PATH = "/apartments";

export interface ApartmentDto {
  id: number;
  number: string;
  area: number;
  ownerName: string;
}

export interface CreateApartmentDto {
  number: string;
  area: number;
  ownerId: number;
}

export async function getAllAprtmentsList(
  getPagedListDto: GetPagedListDto,
  pattern: string
): Promise<ApiResponse<PaginatedList<ApartmentDto>>> {
  return get(`${APARTMENTS_PATH}`, { ...getPagedListDto, pattern: pattern });
}

export async function createAprtment(
  body: CreateApartmentDto
): Promise<ApiResponse<PaginatedList<ApartmentDto>>> {
  return post(`${APARTMENTS_PATH}`, body);
}

export async function getApartmentDetails(
  apartmentId: number
): Promise<ApiResponse<ApartmentDto>> {
  return get(`${APARTMENTS_PATH}/${apartmentId}`);
}

export async function getOwnerApartments(
  getPagedListDto: GetPagedListDto
): Promise<ApiResponse<List<ApartmentDto>>> {
  return get(`${APARTMENTS_PATH}/self`, { ...getPagedListDto });
}
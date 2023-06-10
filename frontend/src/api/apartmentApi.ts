import { GetPagedListDto, List, PaginatedList } from "./accountApi";
import { ApiResponse, get, post, put } from "./api";

const APARTMENTS_PATH = "/apartments";

export interface ApartmentDto {
  id: number;
  number: string;
  area: number;
  ownerName: string;
}

export interface ChangeApartmentOwnerDto {
  id: number;
  newOwnerLogin: string;
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

export async function getApartmentById(
  id: number
): Promise<ApiResponse<ApartmentDto>> {
  return get(`${APARTMENTS_PATH}/${id}`);
}

export async function changeApartmentOwner(
  id: number,
  newOwnerId: number
): Promise<ApiResponse<ChangeApartmentOwnerDto>> {
  return put(`${APARTMENTS_PATH}/${id}/owner`, { newOwnerId });
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
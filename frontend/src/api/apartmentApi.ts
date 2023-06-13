import { GetPagedListDto, List, PaginatedList } from "./accountApi";
import { ApiResponse, get, post, put } from "./api";

const APARTMENTS_PATH = "/apartments";

export interface ApartmentDto {
  id: number;
  number: string;
  area: number;
  ownerName: string;
  ownerId: number;
}

export interface ChangeApartmentOwnerDto {
  id: number;
  newOwnerLogin: string;
}

export interface EditApartmentDto {
  number: string;
  area: number;
}

export interface CreateApartmentDto {
  number: string;
  area: number;
  ownerId: number;
}

export interface AssignWaterMeterDto {
  expiryDate: Date;
  startingValue: number;
  type: string;
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

export async function editApartment(
  id: number,
  body: EditApartmentDto
): Promise<ApiResponse<PaginatedList<ApartmentDto>>> {
  return put(`${APARTMENTS_PATH}/${id}`, body);
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

export async function assignWaterMeterToApartment(
  apartmentId: undefined,
  body: AssignWaterMeterDto
) {
  return post(`${APARTMENTS_PATH}/${apartmentId}/water-meter`, body);
}

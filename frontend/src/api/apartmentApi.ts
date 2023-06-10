import { GetPagedAccountListDto, PaginatedList } from "./accountApi";
import { ApiResponse, get } from "./api";

const ACCOUNTS_PATH = "/apartments";

export interface ApartmentDto {
  id: number;
  number: string;
  area: number;
  ownerId: number;
}

export async function getAllAprtmentsList(
  getPagedListDto: GetPagedAccountListDto,
  pattern: string
): Promise<ApiResponse<PaginatedList<ApartmentDto>>> {
  return get(`${ACCOUNTS_PATH}`, { ...getPagedListDto, pattern: pattern });
}

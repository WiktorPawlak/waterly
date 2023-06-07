import { ListAccountDto } from "./accountApi";
import { ApiResponse, get, post, put } from "./api";

const TARIFFS_PATH = "/tariffs";

export interface PaginatedList<T> {
    data: T[];
    pageNumber: number;
    itemsInPage: number;
    totalPages: number;
}

export interface TariffDto {
    id: number;
    coldWaterPrice: number;
    hotWaterPrice: number;
    trashPrice: number;
    startDate: Date;
    endDate: Date;
    version: number;
}

export interface GetPagedTariffsListDto {
    page: number | null;
    pageSize: number | null;
    order: string;
    orderBy: string;
}

export async function getTariffsList(
    getTariffsListDto: GetPagedTariffsListDto
): Promise<ApiResponse<PaginatedList<TariffDto>>> {
    return post(`${TARIFFS_PATH}/list`, getTariffsListDto);
}

export async function getTariffById(
    id: number,
): Promise<ApiResponse<TariffDto>> {
    return get(`${TARIFFS_PATH}/${id}`);
}

export async function updateTariff(id: number, updatedTariff: TariffDto, etag: string) {
    return put(`${TARIFFS_PATH}/${id}`, updatedTariff,{
        "If-Match": etag,
    });
}
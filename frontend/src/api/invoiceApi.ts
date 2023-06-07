import { post, ApiResponse } from "./api";

const INVOICES_PATH = "/invoices";

export interface GetPagedInvoicesListDto {
  order: string;
  page: number;
}

export interface PaginatedList<T> {
  data: T[];
  pageNumber: number;
  itemsInPage: number;
  totalPages: number;
}

export interface ListInvoiceDto {
  invoiceNumber: string;
  date: string;
  totalCost: number;
  waterUsage: number;
}

export async function getInvoicesList(
  getPagedListDto: GetPagedInvoicesListDto
): Promise<ApiResponse<PaginatedList<ListInvoiceDto>>> {
  return post(`${INVOICES_PATH}/list`, getPagedListDto);
}

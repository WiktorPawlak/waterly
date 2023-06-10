import { post, ApiResponse, get, put } from "./api";

const INVOICES_PATH = "/invoices";

export interface GetPagedInvoicesListDto {
  order: string;
  page: number;
  pageSize: number;
  orderBy: string;
}

export interface PaginatedList<T> {
  data: T[];
  pageNumber: number;
  itemsInPage: number;
  totalPages: number;
}

export interface InvoiceDto {
  id: number;
  invoiceNumber: string;
  date: string;
  totalCost: number;
  waterUsage: number;
  version: number;
}

export async function getInvoicesList(
  getPagedListDto: GetPagedInvoicesListDto
): Promise<ApiResponse<PaginatedList<InvoiceDto>>> {
  return post(`${INVOICES_PATH}/list`, getPagedListDto);
}

export async function getInvoiceById(
  id: number,
): Promise<ApiResponse<InvoiceDto>> {
  return get(`${INVOICES_PATH}/${id}`);
}

export async function updateInvoice(id: number, updatedInvoice: InvoiceDto, etag: string) {
  return put(`${INVOICES_PATH}/${id}`, updatedInvoice,{
      "If-Match": etag,
  });
}
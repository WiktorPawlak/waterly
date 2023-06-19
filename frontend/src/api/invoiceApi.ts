import { ApiResponse, get, post, put } from "./api";
import { GetPagedListDto, PaginatedList } from "./accountApi";

const INVOICES_PATH = "/invoices";

export interface InvoiceDto {
  id: number;
  invoiceNumber: string;
  date: string;
  totalCost: number;
  waterUsage: number;
  version: number;
}

export interface CreateInvoiceDto {
  invoiceNumber: String;
  waterUsage: number;
  totalCost: number;
  date: Date;
}

export async function getInvoicesList(
  getPagedListDto: GetPagedListDto,
  pattern: string
): Promise<ApiResponse<PaginatedList<InvoiceDto>>> {
  return get(`${INVOICES_PATH}`, { ...getPagedListDto, pattern: pattern });
}

export async function getInvoiceById(
  id: number
): Promise<ApiResponse<InvoiceDto>> {
  return get(`${INVOICES_PATH}/${id}`);
}

export async function updateInvoice(
  id: number,
  updatedInvoice: InvoiceDto,
  etag: string
) {
  return put(`${INVOICES_PATH}/${id}`, updatedInvoice, {
    "If-Match": etag,
  });
}

export async function addInvoice(createInvoice: CreateInvoiceDto) {
  return post(`${INVOICES_PATH}`, createInvoice);
}

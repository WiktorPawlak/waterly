import { ApiResponse, get, post, postNoBody, put, putNoBody } from "./api";

const ACCOUNTS_PATH = "/accounts";

export interface EditAccountDetailsDto {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
}

export interface AccountDto {
  id: number;
  login: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  languageTag: string;
  roles: string[];
  active: boolean;
  createdOn: string;
  createdBy: string;
  updatedOn: string;
  updatedBy: string;
  lastSuccessAuth: string;
  lastIncorrectAuth: string;
  lastIpAddress: string;
  incorrectAuthCount: string;
}

export interface PaginatedList<T> {
  data: T[];
  pageNumber: number;
  itemsInPage: number;
  totalPages: number;
}

export interface ListAccountDto {
  id: number;
  login: string;
  firstName: string;
  lastName: string;
  active: boolean;
  roles: string[];
}

export interface AccountSearchPreferencesDto {
  pageSize: number;
  order: string;
  orderBy: string;
}

export interface GetPagedAccountListDto {
  page: number | null;
  pageSize: number | null;
  order: string;
  orderBy: string;
}

export interface AccountActiveStatusDto {
  active: boolean;
}

export async function putAccountDetails(body: EditAccountDetailsDto) {
  return put(`${ACCOUNTS_PATH}/self`, body);
}

export async function putVerifyAccount(token: string) {
  return putNoBody(`${ACCOUNTS_PATH}/confirm-registration?token=` + token);
}

export async function postResendVerificationToken(accountId: string) {
  return postNoBody(`${ACCOUNTS_PATH}/${accountId}/resend-verification-token`);
}

export async function getSelfAccountDetails(): Promise<
  ApiResponse<AccountDto>
> {
  return get(`${ACCOUNTS_PATH}/self`);
}

export async function changeAccountActiveStatus(
  accountId: string,
  body: AccountActiveStatusDto
) {
  return put(`${ACCOUNTS_PATH}/${accountId}/active`, body);
}

export async function getUserById(
  accountId: number
): Promise<ApiResponse<AccountDto>> {
  return get(`${ACCOUNTS_PATH}/${accountId}`);
}

export async function getSelfSearchPreferences(): Promise<
  ApiResponse<AccountSearchPreferencesDto>
> {
  return get(`${ACCOUNTS_PATH}/self/preferences`);
}

export async function getAccountsList(
  getPagedListDto: GetPagedAccountListDto
): Promise<ApiResponse<PaginatedList<ListAccountDto>>> {
  return post(`${ACCOUNTS_PATH}/list`, getPagedListDto);
}

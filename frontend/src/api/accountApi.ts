import { ApiResponse, get, post, put, remove } from "./api";
import { RoleOperation } from "../types";
import { AccountDetailsSchemaType } from "../validation/validationSchemas";

const ACCOUNTS_PATH = "/accounts";

export interface EditAccountDetailsDto {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  languageTag: string;
  version: number;
  twoFAEnabled: boolean;
}

export interface EditEmailDto {
  id: number;
  email: string;
  version: number;
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
  version: number;
  twoFAEnabled: boolean;
}

export interface PaginatedList<T> {
  data: T[];
  pageNumber: number;
  itemsInPage: number;
  totalPages: number;
}

export interface List<T> {
  data: T[];
}

export interface GetPagedListDto {
  page: number | null;
  pageSize: number | null;
  order: string;
  orderBy: string;
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

export interface AccountActiveStatusDto {
  active: boolean;
}

export interface PasswordResetDto {
  token: string;
  newPassword: string;
}

export interface EditRolesDto {
  operation: RoleOperation;
  roles: string[];
}

export interface AccountPasswordDto {
  oldPassword: string;
  newPassword: string;
}

export interface PasswordChangeByAdminDto {
  newPassword: string;
}

export async function postChangePasswordByAdmin(
  email: string,
  body: PasswordChangeByAdminDto
) {
  return post(`${ACCOUNTS_PATH}/password/request-change?email=` + email, body);
}

export async function changeOwnPassword(body: AccountPasswordDto) {
  return put(`${ACCOUNTS_PATH}/self/password`, body);
}

export async function editAccountDetails(
  body: EditAccountDetailsDto,
  etag: string
) {
  return put(`${ACCOUNTS_PATH}/self`, body, {
    "If-Match": etag,
  });
}

export async function editEmail(body: EditEmailDto, etag: string) {
  return put(`${ACCOUNTS_PATH}/self/email`, body, {
    "If-Match": etag,
  });
}

export async function editEmailByAdmin(body: EditEmailDto, etag: string) {
  return put(`${ACCOUNTS_PATH}/${body.id}/email`, body, {
    "If-Match": etag,
  });
}

export async function resendEmailEditMail() {
  return post(`${ACCOUNTS_PATH}/self/email/resend-accept-email`);
}

export async function postAcceptEmail(token: string) {
  return post(`${ACCOUNTS_PATH}/email/accept?token=` + token);
}

export async function putVerifyAccount(token: string) {
  return put(`${ACCOUNTS_PATH}/confirm-registration?token=` + token);
}

export async function postSendResetPasswordEmail(email: string) {
  return post(`${ACCOUNTS_PATH}/password/request-reset?email=` + email);
}

export async function postResetPassword(body: PasswordResetDto) {
  return post(`${ACCOUNTS_PATH}/password/reset`, body);
}

export async function postResendVerificationToken(accountId: string) {
  return post(`${ACCOUNTS_PATH}/${accountId}/resend-verification-token`);
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
  getPagedListDto: GetPagedListDto,
  pattern: string
): Promise<ApiResponse<PaginatedList<ListAccountDto>>> {
  return post(`${ACCOUNTS_PATH}/list`, getPagedListDto, { pattern: pattern });
}

export async function getNotConfirmedAccoutsList(
  getPagedListDto: GetPagedListDto,
  pattern: string
): Promise<ApiResponse<PaginatedList<ListAccountDto>>> {
  return post(`${ACCOUNTS_PATH}/to-verify`, getPagedListDto, {
    pattern: pattern,
  });
}

export async function getAccountNames(
  pattern: string
): Promise<ApiResponse<String[]>> {
  return get(`${ACCOUNTS_PATH}/list/name-suggestions`, { pattern: pattern });
}

export async function getOwnersAccounts(): Promise<
  ApiResponse<ListAccountDto[]>
> {
  return get(`${ACCOUNTS_PATH}/owners`);
}

export async function grantAccountPermissions(
  accountId: number,
  body: EditRolesDto
) {
  return put(`${ACCOUNTS_PATH}/${accountId}/roles`, body);
}

export async function putAccountDetails(
  accountId: number,
  body: EditAccountDetailsDto,
  etag: string
) {
  return put(`${ACCOUNTS_PATH}/${accountId}`, body, {
    "If-Match": etag,
  });
}

export async function createAccountByAdmin(body: AccountDetailsSchemaType) {
  return post(`${ACCOUNTS_PATH}/`, body);
}

export async function acceptAccount(accountId: number) {
  return post(`${ACCOUNTS_PATH}/${accountId}/accept`, null);
}

export async function rejectAccount(accountId: number) {
  return remove(`${ACCOUNTS_PATH}/${accountId}/reject`);
}

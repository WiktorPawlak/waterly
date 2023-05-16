import { EnumValues } from "zod";
import {ApiResponse, get, post, postNoBody, put, putNoBody} from "./api";
import { RoleOperation } from "../types";

const ACCOUNTS_PATH = "/accounts";

export interface EditAccountDetailsDto {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  languageTag: string;
}

export interface EditEmailDto {
  email: string;
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

export enum TokenType {
  Registration = "REGISTRATION",
  PasswordReset = "PASSWORD_RESET",
  ChangePassword = "CHANGE_PASSWORD",
  EmailUpdate = "EMAIL_UPDATE",
}

export interface PasswordResetDto {
  token: string;
  newPassword: string;
  type: TokenType;
}

export interface EditRolesDto {
  operation: RoleOperation,
  roles: string[]
}

export interface AccountPasswordDto {
  oldPassword: string;
  newPassword: string;
}

export async function changeOwnPassword(body: AccountPasswordDto) {
  return put(`${ACCOUNTS_PATH}/self/password`, body);
}

export async function editAccountDetails(body: EditAccountDetailsDto) {
  return put(`${ACCOUNTS_PATH}/self`, body);
}

export async function editEmail(body: EditEmailDto) {
  return put(`${ACCOUNTS_PATH}/self/email`, body);
}

export async function resendEmailEditMail() {
  return post(`${ACCOUNTS_PATH}/self/email/resend-accept-email`);
}

export async function putVerifyAccount(token: string) {
  return putNoBody(`${ACCOUNTS_PATH}/confirm-registration?token=` + token);
}

export async function postSendResetPasswordEmail(email: string) {
  return postNoBody(`${ACCOUNTS_PATH}/password/request-reset?email=` + email);
}

export async function postResetPassword(body: PasswordResetDto) {
  return post(`${ACCOUNTS_PATH}/password/reset`, body);
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


export async function grantAccountPermissions(
  accountId: number,
  body: EditRolesDto
) {
  return put(`${ACCOUNTS_PATH}/${accountId}/roles`, body);
}
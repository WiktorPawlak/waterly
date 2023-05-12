import { ApiResponse, get, postNoBody, put, putNoBody } from "./api";

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

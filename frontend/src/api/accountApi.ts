import { ApiResponse, get, put } from "./api";

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

export async function getSelfAccountDetails(): Promise<
  ApiResponse<AccountDto>
> {
  return get(`${ACCOUNTS_PATH}/self`);
}

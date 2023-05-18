import { post, ApiResponse } from "./api";

interface User {
  login: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  languageTag: string;
}

export interface LoginRequestBody {
  login: string;
  password: string;
}

export interface LoginResponse<T> extends ApiResponse<T> {
  headers?: any;
  token?: string;
}

export async function postLogin<T>(
  body: LoginRequestBody
): Promise<LoginResponse<T>> {
  return post("/auth/login", body);
}

export const registerApi = (user: User, recaptchaResponse: string | null) =>
  post(`/accounts/register?recaptchaResponse=${recaptchaResponse}`, user);

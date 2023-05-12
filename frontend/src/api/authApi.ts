import { post, ApiResponse } from "./api";

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

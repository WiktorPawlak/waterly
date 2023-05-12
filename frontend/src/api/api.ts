import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

const BASE_URL = "http://localhost:8080/ssbd06-0.2.1/api";

export interface ApiResponse<T> {
  status?: number;
  data?: T;
  error?: string;
  token?: string;
}

export async function get<T>(
  url: string,
  params?: Record<string, unknown>
): Promise<ApiResponse<T>> {
  const config: AxiosRequestConfig = {
    method: "get",
    url: BASE_URL + url,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    withCredentials: true,
    params,
  };

  try {
    const response: AxiosResponse<T> = await axios(config);
    return { data: response.data };
  } catch (error: unknown) {
    const message = (error as Error).message;
    return { error: message };
  }
}

export async function getNoResponse(
  url: string,
  params?: Record<string, unknown>
): Promise<number> {
  const config: AxiosRequestConfig = {
    method: "get",
    url: BASE_URL + url,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    withCredentials: true,
    params,
  };

  try {
    const response: AxiosResponse = await axios(config);
    return response.status;
  } catch (error: unknown) {
    return (error as AxiosResponse)?.status || 500;
  }
}

export async function post<T>(url: string, body: any): Promise<ApiResponse<T>> {
  const config: AxiosRequestConfig = {
    method: "POST",
    url: BASE_URL + url,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    withCredentials: true,
    data: body,
  };

  try {
    const response: AxiosResponse<T> = await axios(config);
    return { data: response.data, status: response.status };
  } catch (error: unknown) {
    return { error: (error as Error).message };
  }
}
export async function put<T>(url: string, body: any): Promise<ApiResponse<T>> {
  const config: AxiosRequestConfig = {
    method: "POST",
    url: BASE_URL + url,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    withCredentials: true,
    data: body,
  };

  try {
    const response: AxiosResponse<T> = await axios(config);
    return { data: response.data, status: response.status };
  } catch (error: unknown) {
    return { error: (error as Error).message };
  }
}

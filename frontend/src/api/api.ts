import { AxiosResponse } from "axios";
import { axiosClient } from "./axois/axoisInstance";

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
  return axiosClient.get(url, {
    params: { params }
  }).then((response) => {
    return { data: response.data };
  })
  .catch((error: unknown) => {
    const message = (error as Error).message;
    return { error: message };
  });
}

export async function getNoResponse(
  url: string,
  params?: Record<string, unknown>
): Promise<number> {
  return axiosClient.get(url, {
    params: { params }
  }).then((response) => {
    return response.status;
  })
  .catch((error: unknown) => {
    return (error as AxiosResponse)?.status || 500;
  });
}

export async function post<T>(url: string, body: any): Promise<ApiResponse<T>> {
  return axiosClient.post(url, body)
  .then((response) => {
    return { data: response.data, status: response.status };
  })
  .catch((error: unknown) => {
    return { error: (error as Error).message };
  });
}

export async function postNoBody<T>(url: string): Promise<ApiResponse<T>> {
  return axiosClient.post(url)
  .then((response) => {
    return { data: response.data, status: response.status };
  })
  .catch((error: unknown) => {
    return { error: (error as Error).message };
  });
}

export async function put<T>(url: string, body: any): Promise<ApiResponse<T>> {
  return axiosClient.put(url, body)
  .then((response) => {
    return { data: response.data, status: response.status };
  })
  .catch((error: unknown) => {
    return { error: (error as Error).message };
  });
}

export async function putNoBody<T>(url: string): Promise<ApiResponse<T>> {
  return axiosClient.put(url)
  .then((response) => {
    return { data: response.data, status: response.status };
  })
  .catch((error: unknown) => {
    return { error: (error as Error).message };
  });
}

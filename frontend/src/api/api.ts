import { AxiosResponse } from "axios";
import { axiosClient } from "./axois/axoisInstance";
import { errorUtil } from "zod/lib/helpers/errorUtil";

export interface ApiResponse<T> {
  headers?: any;
  status?: number;
  data?: T;
  error?: string;
  token?: string;
}

export async function get<T>(
  url: string,
  params?: Record<string, unknown>
): Promise<ApiResponse<T>> {
  return axiosClient
    .get(url, {
      params: params,
    })
    .then((response) => {
      return {
        data: response.data,
        headers: response.headers,
        status: response.status,
      };
    })
    .catch((error: any) => {
      return { error: error.response?.data?.message };
    });
}

export async function getNoResponse(
  url: string,
  params?: Record<string, unknown>
): Promise<number> {
  return axiosClient
    .get(url, {
      params: { params },
    })
    .then((response) => {
      return response.status;
    })
    .catch((error: unknown) => {
      return (error as AxiosResponse)?.status || 500;
    });
}

export async function post<T>(
  url: string,
  body?: any,
  params?: Record<string, unknown>
): Promise<ApiResponse<T>> {
  return axiosClient
    .post(url, body, {
      params: params,
    })
    .then((response) => {
      return { data: response.data, status: response.status };
    })
    .catch(function (error) {
      return { error: error.response?.data?.message };
    });
}

export async function put<T>(
  url: string,
  body?: any,
  headers?: any
): Promise<ApiResponse<T>> {
  return axiosClient
    .put(url, body, {
      headers: headers,
    })
    .then((response) => {
      return { data: response.data, status: response.status };
    })
    .catch(function (error) {
      return { error: error.response?.data?.message };
    });
}

export async function remove<T>(
  url: string,
  headers?: any
): Promise<ApiResponse<T>> {
  return axiosClient
    .delete(url, {
      headers: headers,
    })
    .then((response) => {
      return { data: response.data, status: response.status };
    })
    .catch(function (error) {
      return { error: error.response?.data?.message };
    });
}
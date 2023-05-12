import { InternalAxiosRequestConfig } from 'axios';

export const axiosAuthInterceptor = (config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('jwtToken');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
};
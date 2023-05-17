import axios, { AxiosInstance } from 'axios';
import { axiosAuthInterceptor } from './axoisAuthInterceptor';

const API_BASE_URL = "http://localhost:8080/api";

export const axiosClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json"
  },
  withCredentials: true
});

axiosClient.interceptors.request.use(axiosAuthInterceptor);

export default axiosClient;
import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api';

export const api = axios.create({
  baseURL,
  timeout: 10000
});

api.interceptors.response.use(
  response => response,
  error => {
    const message = error.response?.data?.error?.message ?? error.message;
    return Promise.reject(new Error(message));
  }
);

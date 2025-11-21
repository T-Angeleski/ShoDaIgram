import axios, { AxiosError, AxiosInstance } from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

// Logging in development mode
if (import.meta.env.DEV) {
  apiClient.interceptors.request.use((config) => {
    console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  });
}

// Error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response) {
      console.error("[API Error]", error.response.data);
      return Promise.reject(error.response.data);
    } else if (error.request) {
      const networkError = {
        status: 0,
        message: "Network error. Please check your connection.",
        timestamp: new Date().toISOString(),
        error: "Network Error",
        path: error.config?.url || "",
      };
      console.error("[Network Error]", networkError);
      return Promise.reject(networkError);
    } else {
      const unknownError = {
        status: 0,
        message: error.message,
        timestamp: new Date().toISOString(),
        error: "Unknown Error",
        path: "",
      };
      console.error("[Unknown Error]", unknownError);
      return Promise.reject(unknownError);
    }
  },
);

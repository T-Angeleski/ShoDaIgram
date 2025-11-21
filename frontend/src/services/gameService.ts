import { apiClient } from "./api";

export const gameService = {
  async testConnection() {
    const response = await apiClient.get("/games", {
      params: { page: 0, size: 5 },
    });
    return response.data;
  },
};

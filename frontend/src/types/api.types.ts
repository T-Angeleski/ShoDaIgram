/**
 * Tag category enum
 */
export enum TagCategory {
  GENRE = "GENRE",
  THEME = "THEME",
  PLATFORM = "PLATFORM",
  GAME_MODE = "GAME_MODE",
  DEVELOPER = "DEVELOPER",
  PUBLISHER = "PUBLISHER",
  FRANCHISE = "FRANCHISE",
  KEYWORD = "KEYWORD",
  PLAYER_PERSPECTIVE = "PLAYER_PERSPECTIVE",
  OTHER = "OTHER",
}

/**
 * Standard error response from backend
 */
export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: string;
  error: string;
  path: string;
}

/**
 * Pagination request parameters
 */
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

/**
 * Game list request parameters
 */
export interface GameListParams extends PaginationParams {
  tags?: string[];
  minRating?: number;
  maxRating?: number;
  minYear?: number;
  maxYear?: number;
}

/**
 * Search request parameters
 */
export interface SearchParams extends PaginationParams {
  query: string;
}

/**
 * Tag list request parameters
 */
export interface TagListParams extends PaginationParams {
  category?: TagCategory;
  search?: string;
  limit?: number;
}

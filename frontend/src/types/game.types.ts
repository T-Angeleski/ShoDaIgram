import { TagCategory } from "./api.types";

/**
 * Game summary for lists and cards
 */
export interface GameSummaryDto {
  id: number;
  name: string;
  slug: string;
  rating: number | null;
  ratingCount: number;
  releaseDate: string | null;
  backgroundImageUrl: string | null;
}

/**
 * Detailed game information
 */
export interface GameDetailDto {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  rating: number | null;
  ratingCount: number;
  releaseDate: string | null;
  backgroundImageUrl: string | null;
  websiteUrl: string | null;
  tags: TagDto[];
  tagsByCategory: Record<string, TagDto[]>;
}

/**
 * Tag entity
 */
export interface TagDto {
  id: number;
  name: string;
  normalizedName: string;
  category: TagCategory;
}

/**
 * Tag with game count for browsing
 */
export interface TagSummaryDto {
  id: number;
  name: string;
  normalizedName: string;
  category: TagCategory;
  gameCount: number;
}

/**
 * Paginated game list response
 */
export interface GamePageDto {
  games: GameSummaryDto[];
  page: number;
  pageSize: number;
  totalResults: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

/**
 * Game search result with BM25 ranking
 */
export interface GameSearchDto {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  rating: number | null;
  ratingCount: number;
  releaseDate: string | null;
  backgroundImageUrl: string | null;
  searchRank: number;
}

/**
 * Search results response
 */
export interface SearchResultDto {
  games: GameSearchDto[];
  query: string;
  totalResults: number;
  page: number;
  pageSize: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

/**
 * Tag list response
 */
export interface TagListResponse {
  tags: TagSummaryDto[];
  totalResults: number;
  page: number;
  pageSize: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

/**
 * Games by tag response
 */
export interface TagGamesResponseDto {
  tag: {
    name: string;
    normalizedName: string;
    category: TagCategory;
  };
  games: GameSummaryDto[];
  page: number;
  pageSize: number;
  totalResults: number;
  totalPages: number;
}

import { GameSummaryDto } from "./game.types";

/**
 * Similar game recommendation
 */
export interface SimilarGameDto {
  gameId: number;
  name: string;
  slug: string;
  rating: number | null;
  ratingCount: number | null;
  releaseDate: string | null;
  backgroundImageUrl: string | null;
  similarityScore: number;
  similarityType: SimilarityType;
}

/**
 * Similar game with explainability
 */
export interface SimilarGameWithReasonsDto {
  gameId: number;
  name: string;
  slug: string;
  rating: number | null;
  ratingCount: number | null;
  releaseDate: string | null;
  backgroundImageUrl: string | null;
  similarityScore: number;
  similarityType: SimilarityType;
  matchReasons: MatchReason[];
}

/**
 * Match reason for explainability
 */
export interface MatchReason {
  type: MatchReasonType;
  details: string;
  contribution: number;
}

/**
 * Recommendation response wrapper
 */
export interface RecommendationResponse {
  recommendations: SimilarGameDto[];
  detailedRecommendations: SimilarGameWithReasonsDto[] | null;
  metadata: RecommendationMetadata;
}

/**
 * Recommendation metadata
 */
export interface RecommendationMetadata {
  sourceGameId: number;
  algorithm: string;
  requestedLimit: number;
  returnedCount: number;
  explainability: boolean;
}

/**
 * Similarity type enum
 */
export enum SimilarityType {
  TFIDF = "TFIDF",
  IGDB_SIMILAR = "IGDB_SIMILAR",
  HYBRID = "HYBRID",
}

/**
 * Match reason type enum
 */
export enum MatchReasonType {
  GENRE_MATCH = "GENRE_MATCH",
  THEME_MATCH = "THEME_MATCH",
  FRANCHISE_MATCH = "FRANCHISE_MATCH",
  DESCRIPTION_SIMILARITY = "DESCRIPTION_SIMILARITY",
}

/**
 * Game filter response
 */
export interface GameFilterResponse {
  games: GameSummaryDto[];
  appliedFilters: AppliedFilters;
  page: number;
  pageSize: number;
  totalResults: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

/**
 * Applied filters metadata
 */
export interface AppliedFilters {
  tags: string[] | null;
  minRating: number | null;
  maxRating: number | null;
  minYear: number | null;
  maxYear: number | null;
  platforms: string[] | null;
}

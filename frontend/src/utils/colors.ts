/**
 * Centralized color constants for the application
 * Using theme-consistent colors throughout
 */

// Brand Colors - Purple Gradient Theme
export const BRAND_COLORS = {
  PRIMARY_START: "#667eea",
  PRIMARY_END: "#764ba2",
  GRADIENT: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
  GRADIENT_REVERSE: "linear-gradient(135deg, #764ba2 0%, #667eea 100%)",
} as const;

// Rating Colors (0-10 scale)
export const RATING_COLORS = {
  EXCELLENT: "#22c55e", // 8.5+ (Green)
  GOOD: "#eab308", // 7.0-8.5 (Yellow)
  AVERAGE: "#f97316", // 5.0-7.0 (Orange)
  POOR: "#ef4444", // <5.0 (Red)
} as const;

// Match Reason Colors (for recommendation badges)
export const MATCH_REASON_COLORS = {
  GENRE_MATCH: "#3b82f6", // Blue
  THEME_MATCH: "#8b5cf6", // Purple
  FRANCHISE_MATCH: "#ec4899", // Pink
  DESCRIPTION_SIMILARITY: "#10b981", // Green
  DEFAULT: "#64748b", // Slate Gray
} as const;

// Similarity Score Colors (0-1 scale)
export const SIMILARITY_COLORS = {
  VERY_HIGH: "#22c55e", // 0.8+ (Green)
  HIGH: "#f59e0b", // 0.6-0.8 (Amber)
  MODERATE: "#64748b", // <0.6 (Gray)
} as const;

// UI Colors
export const UI_COLORS = {
  BACKGROUND_GRAY: "#f9fafb",
  DIVIDER: "rgba(0, 0, 0, 0.12)",
  TEXT_PRIMARY: "rgba(0, 0, 0, 0.87)",
  TEXT_SECONDARY: "rgba(0, 0, 0, 0.6)",
  WHITE: "#ffffff",
  SHADOW_LIGHT: "rgba(0, 0, 0, 0.15)",
  SHADOW_MEDIUM: "rgba(0, 0, 0, 0.2)",
} as const;

/**
 * Helper function to get rating color based on score
 * @param rating - Rating value (0-10)
 * @returns Color hex code
 */
export const getRatingColor = (rating: number): string => {
  if (rating >= 8.5) return RATING_COLORS.EXCELLENT;
  if (rating >= 7) return RATING_COLORS.GOOD;
  if (rating >= 5) return RATING_COLORS.AVERAGE;
  return RATING_COLORS.POOR;
};

/**
 * Helper function to get similarity color based on score
 * @param score - Similarity score (0-1)
 * @returns Color hex code
 */
export const getSimilarityColor = (score: number): string => {
  if (score >= 0.8) return SIMILARITY_COLORS.VERY_HIGH;
  if (score >= 0.6) return SIMILARITY_COLORS.HIGH;
  return SIMILARITY_COLORS.MODERATE;
};

/**
 * Helper function to get match reason color
 * @param reasonType - Type of match reason
 * @returns Color hex code
 */
export const getMatchReasonColor = (reasonType: string): string => {
  return (
    MATCH_REASON_COLORS[reasonType as keyof typeof MATCH_REASON_COLORS] ??
    MATCH_REASON_COLORS.DEFAULT
  );
};

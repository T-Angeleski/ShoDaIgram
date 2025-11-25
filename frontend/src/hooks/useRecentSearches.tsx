import { useCallback, useState } from "react";

const STORAGE_KEY = "recentSearches";
const MAX_RECENT_SEARCHES = 5;

/**
 * Safely loads recent searches from localStorage.
 * Returns empty array if parsing fails or data is invalid.
 */
const loadRecentSearches = (): string[] => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      const parsed = JSON.parse(stored);
      if (Array.isArray(parsed)) {
        return parsed;
      }
    }
  } catch (error) {
    console.error("Failed to load recent searches:", error);
  }
  return [];
};

/**
 * Hook for managing recent search queries with localStorage persistence.
 * Stores up to 5 recent searches, automatically deduplicates (case-insensitive),
 * and filters out queries shorter than 3 characters.
 *
 * @returns Object with recentSearches array, addSearch, and clearSearches functions
 * @example
 * const { recentSearches, addSearch } = useRecentSearches();
 * addSearch("witcher"); // Adds to recent searches
 */
export const useRecentSearches = () => {
  const [recentSearches, setRecentSearches] =
    useState<string[]>(loadRecentSearches);

  const addSearch = useCallback((query: string) => {
    const trimmed = query.trim();
    if (!trimmed || trimmed.length < 3) return;

    setRecentSearches((prev) => {
      const filtered = prev.filter(
        (s) => s.toLowerCase() !== trimmed.toLowerCase(),
      );
      const updated = [trimmed, ...filtered].slice(0, MAX_RECENT_SEARCHES);

      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
      } catch (error) {
        console.error("Failed to save recent searches:", error);
      }

      return updated;
    });
  }, []);

  const clearSearches = useCallback(() => {
    setRecentSearches([]);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (error) {
      console.error("Failed to clear recent searches:", error);
    }
  }, []);

  return {
    recentSearches,
    addSearch,
    clearSearches,
  };
};

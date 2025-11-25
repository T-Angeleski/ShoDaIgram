import { memo } from "react";
import { Box } from "@mui/material";
import styled from "styled-components";

interface HighlightedTextProps {
  text: string;
  query: string;
}

const Mark = styled.mark`
  background-color: #fef08a;
  color: inherit;
  font-weight: 600;
  padding: 0 2px;
  border-radius: 2px;
`;

/** Common stop words filtered out from keyword highlighting */
const commonWords = new Set([
  "a",
  "an",
  "the",
  "in",
  "on",
  "at",
  "to",
  "for",
  "of",
  "with",
  "by",
  "and",
  "or",
  "but",
]);

/**
 * Highlights matching keywords from search query in text.
 * Filters out common stop words and highlights only meaningful terms (3+ chars).
 * Uses yellow background with semi-bold text for highlights.
 *
 * @param text - The text to search and highlight within
 * @param query - The search query containing keywords to highlight
 * @returns JSX with matched keywords wrapped in <Mark> components
 */
const HighlightedText = ({ text, query }: HighlightedTextProps) => {
  if (!query.trim()) {
    return <Box component="span">{text}</Box>;
  }

  const keywords = query
    .toLowerCase()
    .split(/\s+/)
    .filter((word) => word.length > 2 && !commonWords.has(word));

  if (keywords.length === 0) {
    return <Box component="span">{text}</Box>;
  }

  // Create regex pattern to match any keyword (case-insensitive)
  const pattern = new RegExp(`(${keywords.join("|")})`, "gi");
  const parts = text.split(pattern);

  return (
    <Box component="span">
      {parts.map((part, index) => {
        const isMatch = keywords.some(
          (keyword) => part.toLowerCase() === keyword.toLowerCase(),
        );
        return isMatch ? (
          <Mark key={`${part}-${index}`}>{part}</Mark>
        ) : (
          <span key={`${part}-${index}`}>{part}</span>
        );
      })}
    </Box>
  );
};

export default memo(HighlightedText);

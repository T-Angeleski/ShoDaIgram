import { Pagination as MuiPagination, Typography } from "@mui/material";

import { PaginationContainer } from "./styled";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalResults: number;
  onPageChange: (page: number) => void;
}

const Pagination = ({
  currentPage,
  totalPages,
  totalResults,
  onPageChange,
}: PaginationProps) => {
  const handleChange = (_event: React.ChangeEvent<unknown>, page: number) => {
    onPageChange(page - 1);
  };

  return (
    <PaginationContainer>
      <Typography variant="body2" color="text.secondary">
        Page {currentPage + 1} of {totalPages} ({totalResults.toLocaleString()}{" "}
        results)
      </Typography>
      <MuiPagination
        count={totalPages}
        page={currentPage + 1}
        onChange={handleChange}
        color="primary"
        showFirstButton
        showLastButton
      />
    </PaginationContainer>
  );
};

export default Pagination;

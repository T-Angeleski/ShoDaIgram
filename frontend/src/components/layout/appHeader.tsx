import { useNavigate } from "react-router-dom";
import SportsEsportsIcon from "@mui/icons-material/SportsEsports";
import { Button, Toolbar, Typography } from "@mui/material";

import { HeaderContent, Logo, NavLinks, StyledAppBar } from "./styled";

const AppHeader = () => {
  const navigate = useNavigate();

  return (
    <StyledAppBar position="fixed">
      <Toolbar disableGutters>
        <HeaderContent>
          <Logo onClick={() => navigate("/")}>
            <SportsEsportsIcon fontSize="large" />
            <Typography variant="h6" component="div" fontWeight={700}>
              ShoDaIgram
            </Typography>
          </Logo>

          <NavLinks>
            <Button color="inherit" onClick={() => navigate("/")}>
              Home
            </Button>
            <Button color="inherit" onClick={() => navigate("/games")}>
              Browse
            </Button>
            <Button color="inherit" onClick={() => navigate("/search")}>
              Search
            </Button>
          </NavLinks>
        </HeaderContent>
      </Toolbar>
    </StyledAppBar>
  );
};

export default AppHeader;

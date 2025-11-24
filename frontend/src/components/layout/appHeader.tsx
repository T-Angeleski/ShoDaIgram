import { useLocation, useNavigate } from "react-router-dom";
import SportsEsportsIcon from "@mui/icons-material/SportsEsports";
import { Button, Toolbar, Typography } from "@mui/material";

import { HeaderContent, Logo, NavLinks, StyledAppBar } from "./styled";

const AppHeader = () => {
  const navigate = useNavigate();
  const location = useLocation();

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
            <Button
              color="inherit"
              onClick={() => navigate("/")}
              sx={{
                borderBottom:
                  location.pathname === "/" ? "2px solid white" : "none",
                borderRadius: 0,
              }}
            >
              Home
            </Button>
            <Button
              color="inherit"
              onClick={() => navigate("/games")}
              sx={{
                borderBottom: location.pathname.startsWith("/games")
                  ? "2px solid white"
                  : "none",
                borderRadius: 0,
              }}
            >
              Browse
            </Button>
            <Button
              color="inherit"
              onClick={() => navigate("/search")}
              sx={{
                borderBottom:
                  location.pathname === "/search" ? "2px solid white" : "none",
                borderRadius: 0,
              }}
            >
              Search
            </Button>
          </NavLinks>
        </HeaderContent>
      </Toolbar>
    </StyledAppBar>
  );
};

export default AppHeader;

import CategoryIcon from "@mui/icons-material/Category";
import DescriptionIcon from "@mui/icons-material/Description";
import LocalOfferIcon from "@mui/icons-material/LocalOffer";
import WorkspacesIcon from "@mui/icons-material/Workspaces";
import { Tooltip } from "@mui/material";

import { MatchReason, MatchReasonType } from "../../types/recommendation.types";

import { MatchBadge } from "./styled";

interface MatchReasonBadgeProps {
  reason: MatchReason;
}

const getReasonConfig = (type: MatchReasonType) => {
  switch (type) {
    case MatchReasonType.GENRE_MATCH:
      return { icon: <CategoryIcon fontSize="small" />, label: "Genre" };
    case MatchReasonType.THEME_MATCH:
      return { icon: <LocalOfferIcon fontSize="small" />, label: "Theme" };
    case MatchReasonType.FRANCHISE_MATCH:
      return { icon: <WorkspacesIcon fontSize="small" />, label: "Franchise" };
    case MatchReasonType.DESCRIPTION_SIMILARITY:
      return { icon: <DescriptionIcon fontSize="small" />, label: "Content" };
    default:
      return { icon: <CategoryIcon fontSize="small" />, label: "Match" };
  }
};

const MatchReasonBadge = ({ reason }: MatchReasonBadgeProps) => {
  const config = getReasonConfig(reason.type);

  return (
    <Tooltip title={reason.details} arrow>
      <MatchBadge $reasonType={reason.type}>
        {config.icon}
        <span>{config.label}</span>
      </MatchBadge>
    </Tooltip>
  );
};

export default MatchReasonBadge;

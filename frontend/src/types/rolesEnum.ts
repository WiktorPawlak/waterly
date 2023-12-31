import { theme } from "../common";

export const roles = {
  administrator: "ADMINISTRATOR",
  facilityManager: "FACILITY_MANAGER",
  owner: "OWNER",
  anonymous: "ANONYMOUS"
};

export const RolesEnum = {
  OWNER: 2,
  FACILITY_MANAGER: 1,
  ADMINISTRATOR: 0,
};

export const statusClasses = {
  OWNER: {
    className: "OWNER",
    color: theme.palette.primary.main,
  },
  FACILITY_MANAGER: {
    className: "FACILITY_MANAGER",
    color:
      theme.palette.secondary[
        50 as unknown as keyof typeof theme.palette.secondary
      ],
  },
  ADMINISTRATOR: {
    className: "ADMINISTRATOR",
    color:
      theme.palette.secondary[
        100 as unknown as keyof typeof theme.palette.secondary
      ],
  },
};

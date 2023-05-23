export const PATHS = {
  LOGIN: "/login",
  REGISTER: "/register",
  WAIT_FOR_VERIFY: "/wait-for-verify",
  EDIT_PROFILE: "/edit-profile",
  RESET_PASSWORD: "/password/reset",
  VERIFY_ACCOUNT: "/verify-account",

  ACCOUNT_DETAILS: "/accounts/:id/details",
  MANAGE_USERS: "/manage-users",
  ACCEPT_EMAIL: "/accept-email",
  VERIFY_USERS: "/verify-users",

  NOT_FOUND: "/not-found",
} as const;

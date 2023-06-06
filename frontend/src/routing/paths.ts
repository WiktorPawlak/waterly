export const PATHS = {
  HOME: "/",
  LOGIN: "/login",
  REGISTER: "/register",
  WAIT_FOR_VERIFY: "/wait-for-verify",
  EDIT_PROFILE: "/profile",
  RESET_PASSWORD: "/password-reset",
  VERIFY_ACCOUNT: "/verify-account",

  ACCOUNT_DETAILS: "/accounts/:id",
  MANAGE_USERS: "/accounts",
  ACCEPT_EMAIL: "/accept-email",
  VERIFY_USERS: "/verify-users",

  NOT_FOUND: "/not-found",

  MANAGE_TARIFFS: "/tariffs",
} as const;

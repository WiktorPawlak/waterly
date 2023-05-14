const apiErrores = new Map<string, string>([
  [
    "ERROR.ACCOUNT_WITH_PHONE_NUMBER_EXIST",
    "apiError.accountWithPhoneNumberExist",
  ],
  ["ERROR.ACCOUNT_WITH_EMAIL_EXIST", "apiError.accountWithEmailExist"],
]);

const UNKONW_ERROR = "apiError.unkownError";

export const resolveApiError = (errorMessage: string | undefined) => {
  if (!errorMessage) {
    return UNKONW_ERROR;
  }
  return apiErrores.get(errorMessage) ?? UNKONW_ERROR;
};

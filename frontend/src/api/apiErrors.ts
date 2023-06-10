const apiErrors = new Map<string, string>([
    ["ERROR.ACCOUNT_WITH_PHONE_NUMBER_EXIST", "apiError.accountWithPhoneNumberExist"],
    ["ERROR.ACCOUNT_WITH_EMAIL_EXIST", "apiError.accountWithEmailExist"],
    ["ERROR.ACCOUNT_WITH_LOGIN_EXIST", "apiError.accountWithLoginExist"],
    ["ERROR.ACCESS_DENIED", "apiError.accessDenied"],
    ["ERROR.CANNOT_MODIFY_PERMISSIONS", "apiError.permissionsAlreadyAdded"],
    ["ERROR.FORBIDDEN_OPERATION", "apiError.forbiddenOperation"],
    ["ERROR.IDENTICAL_PASSWORDS", "apiError.identicalPasswords"],
    ["ERROR.TRANSACTION_ROLLBACK", "apiError.transactionRollback"],
    ["ERROR.OPTIMISTIC_LOCK", "apiError.optimisticLock"],
    ["ERROR.UNSUPPORTED_OPERATION", "apiError.unsupportedOperation"],
    ["ERROR.TOKEN_EXCEEDED_HALF_TIME", "apiError.exceededToken"],
    ["ERROR.ACCOUNT_NOT_FOUND", "apiError.accountNotFound"],
    ["ERROR.ENTITY_INTEGRITY_VIOLATED", "apiError.entityIntegrityViolated"],
    ["ERROR.ACCOUNT_SEARCH_PREFERENCES_NOT_FOUND", "apiError.searchPreferencesNotFound"],
    ["ERROR.NOT_AUTHORIZED", "apiError.notAuthorized"],
    ["ERROR.AUTHENTICATION", "apiError.authenticationError"],
    ["ERROR.NOT_MATCHING_PASSWORDS", "apiError.passwordsNotMatch"],
    ["ERROR.RESOURCE_NOT_FOUND", "apiError.resourceNotFound"],
    ["ERROR.NO_MATCHING_EMAILS", "apiError.emailNotMatch"],
    ["ERROR.NOT_ACTIVE_ACCOUNT", "apiError.accountNotActive"],
    ["ERROR.NOT_CONFIRMED_ACCOUNT", "apiError.accountNotConfirmed"],
    ["ERROR.ERROR_ACCOUNT_NOT_WAITING_FOR_CONFIRMATION", "apiError.accountNotWaitingForConfirmation"],
    ["ERROR.ACCOUNT_LOCKED", "apiError.accountLocked"],
    ["ERROR.TARIFFS_COLIDING", "apiError.tariffsColiding"],
    ["ERROR.INVALID_TARIFF_PERIOD", "apiError.invalidTariffPeriod"],
    ["ERROR.INVOICE_NUMBER_EXISTS", "apiError.invoiceNumberExists"],
    ["ERROR.INVOICES_COLLIDING", "apiError.invoicesColliding"],
    ["VALIDATION.ACCOUNT_INVALID_ORDERBY", "validation.accountInvalidOrderBy"],
    ["VALIDATION.EMAIL", "validation.emailInvalid"],
    ["VALIDATION.EMAIL_SIZE", "validation.emailInvalidSize"],
    ["VALIDATION.EMAIL_PATTERN", "validation.emailInvalidPattern"],
    ["VALIDATION.FIRST_NAME_SIZE", "validation.firstNameInvalidSize"],
    ["VALIDATION.FIRST_NAME_PATTERN", "validation.firstNameInvalidPattern"],
    ["VALIDATION.FIRST_NAME", "validation.firstNameInvalid"],
    ["VALIDATION.PASSWORD_HASH_SIZE", "validation.passwordHashInvalidSize"],
    ["VALIDATION.PASSWORD_HASH", "validation.passwordHashInvalid"],
    ["VALIDATION.LANGUAGE_TAG_PATTERN", "validation.languageTagInvalidPattern"],
    ["VALIDATION.LANGUAGE_TAG", "validation.languageTagInvalid"],
    ["VALIDATION.LAST_NAME_SIZE", "validation.lastNameInvalidSize"],
    ["VALIDATION.LAST_NAME_PATTERN", "validation.lastNameInvalidPattern"],
    ["VALIDATION.LAST_NAME", "validation.lastNameInvalid"],
    ["VALIDATION.LOGIN_SIZE", "validation.loginInvalidSize"],
    ["VALIDATION.LOGIN_PATTERN", "validation.loginInvalidPattern"],
    ["VALIDATION.LOGIN", "validation.loginInvalid"],
    ["VALIDATION.ORDER_NULL", "validation.orderInvalidNull"],
    ["VALIDATION.ORDER_PATTERN", "validation.orderInvalidPattern"],
    ["VALIDATION.ORDER_INVALID", "validation.orderInvalid"],
    ["VALIDATION.PAGE", "validation.pageInvalid"],
    ["VALIDATION.PAGE_SIZE", "validation.pageSizeInvalid"],
    ["VALIDATION.PASSWORD", "validation.passwordInvalid"],
    ["VALIDATION.PERMISSION_PATTERN", "validation.permissionInvalidPattern"],
    ["VALIDATION.PERMISSION", "validation.permissionInvalid"],
    ["VALIDATION.PHONE_NUMBER_SIZE", "validation.phoneNumberInvalidSize"],
    ["VALIDATION.PHONE_NUMBER_PATTERN", "validation.phoneNumberInvalidPattern"],
    ["VALIDATION.PHONE_NUMBER", "validation.phoneNumberInvalid"],
    ["VALIDATION.UUID_PATTERN", "validation.uuidInvalidPattern"],
    ["VALIDATION.UUID", "validation.uuidInvalid"],
]);

const UNKNOWN_ERROR = "apiError.unknownError";

export const resolveApiError = (errorMessage: string | undefined) => {
    if (!errorMessage) {
        return UNKNOWN_ERROR;
    }
    return apiErrors.get(errorMessage) ?? UNKNOWN_ERROR;
};

import { z } from "zod";

export const editEmailSchema = z.object({
  email: z
    .string()
    .min(5, "editAccountDetailsPage.validation.email.min")
    .max(320, "editAccountDetailsPage.validation.email.max")
    .regex(
      /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/,
      "editAccountDetailsPage.validation.email.syntax"
    ),
});

export const resetPasswordEmailSchema = z.object({
  email: z
    .string()
    .min(5, "editAccountDetailsPage.validation.email.min")
    .max(320, "editAccountDetailsPage.validation.email.max")
    .regex(
      /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/,
      "editAccountDetailsPage.validation.email.syntax"
    ),
});

export const editEmailByAdminSchema = z.object({
  email: z
    .string()
    .min(5, "editAccountDetailsPage.validation.email.min")
    .max(320, "editAccountDetailsPage.validation.email.max")
    .regex(
      /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/,
      "editAccountDetailsPage.validation.email.syntax"
    ),
});

export const editAccountSchema = z.object({
  firstName: z
    .string()
    .min(2, "editAccountDetailsPage.validation.firstName.min")
    .max(50, "editAccountDetailsPage.validation.firstName.max")
    .regex(/^\p{L}+$/u, "editAccountDetailsPage.validation.firstName.syntax"),
  lastName: z
    .string()
    .min(2, "editAccountDetailsPage.validation.lastName.min")
    .max(50, "editAccountDetailsPage.validation.lastName.max")
    .regex(
      /^\p{L}+(?:-\p{L}+)*$/u,
      "editAccountDetailsPage.validation.lastName.syntax"
    ),
  phoneNumber: z
    .string()
    .min(8, "editAccountDetailsPage.validation.phoneNumber.min")
    .max(9, "editAccountDetailsPage.validation.phoneNumber.max")
    .regex(/^\d*$/, "editAccountDetailsPage.validation.phoneNumber.syntax"),
});

export const createApartmentSchema = z
  .object({
    number: z
      .string()
      .min(1, "apartmentPage.validation.number.min")
      .max(20, "apartmentPage.validation.number.max")
      .regex(/^[\p{L}0-9._-]+$/u, "apartmentPage.validation.number.syntax"),
    area: z
      .string()
      .regex(/^\d*(\.\d{0,2})?$/, "apartmentPage.validation.area.decimal"),
  })
  .refine(
    (data) => parseFloat(data.area) >= 1 && parseFloat(data.area) < 1000,
    {
      message: "apartmentPage.validation.area.value",
      path: ["area"],
    }
  );

export const editApartmentSchema = z
  .object({
    number: z
      .string()
      .min(1, "apartmentPage.validation.number.min")
      .max(20, "apartmentPage.validation.number.max")
      .regex(/^[\p{L}0-9._-]+$/u, "apartmentPage.validation.number.syntax"),
    area: z
      .string()
      .regex(/^\d*(\.\d{0,2})?$/, "apartmentPage.validation.area.decimal"),
  })
  .refine(
    (data) => parseFloat(data.area) >= 1 && parseFloat(data.area) < 1000,
    {
      message: "apartmentPage.validation.area.value",
      path: ["area"],
    }
  );

export const accountDetailsSchema = z
  .object({
    email: z
      .string()
      .min(5, "editAccountDetailsPage.validation.email.min")
      .max(320, "editAccountDetailsPage.validation.email.max")
      .regex(
        /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/,
        "editAccountDetailsPage.validation.email.syntax"
      ),
    firstName: z
      .string()
      .min(2, "editAccountDetailsPage.validation.firstName.min")
      .max(50, "editAccountDetailsPage.validation.firstName.max")
      .regex(/^\p{L}+$/u, "editAccountDetailsPage.validation.firstName.syntax"),
    lastName: z
      .string()
      .min(2, "editAccountDetailsPage.validation.lastName.min")
      .max(50, "editAccountDetailsPage.validation.lastName.max")
      .regex(
        /^\p{L}+(?:-\p{L}+)*$/u,
        "editAccountDetailsPage.validation.lastName.syntax"
      ),
    phoneNumber: z
      .string()
      .min(8, "editAccountDetailsPage.validation.phoneNumber.min")
      .max(9, "editAccountDetailsPage.validation.phoneNumber.max")
      .regex(/^\d*$/, "editAccountDetailsPage.validation.phoneNumber.syntax"),
    login: z
      .string()
      .min(3, "logInPage.validation.login.min")
      .max(36, "logInPage.validation.login.max"),
    password: z
      .string()
      .min(8, "logInPage.validation.password.min")
      .max(32, "logInPage.validation.password.max"),
    confirmPassword: z
      .string()
      .min(8, "logInPage.validation.confirmPassword.min")
      .max(32, "logInPage.validation.confirmPassword.max"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "logInPage.validation.passwordsMatch",
    path: ["confirmPassword"],
  });

export const loginSchema = z.object({
  login: z
    .string()
    .min(3, "logInPage.validation.login.min")
    .max(36, "logInPage.validation.login.max"),
  password: z
    .string()
    .min(8, "logInPage.validation.password.min")
    .max(32, "logInPage.validation.password.max"),
});

export const editAccountDetailsSchema = z.object({
  firstName: z
    .string()
    .min(2, "editAccountDetailsPage.validation.firstName.min")
    .max(50, "editAccountDetailsPage.validation.firstName.max")
    .regex(/^\p{L}+$/u, "editAccountDetailsPage.validation.firstName.syntax"),
  lastName: z
    .string()
    .min(2, "editAccountDetailsPage.validation.lastName.min")
    .max(50, "editAccountDetailsPage.validation.lastName.max")
    .regex(
      /^\p{L}+(?:-\p{L}+)*$/u,
      "editAccountDetailsPage.validation.lastName.syntax"
    ),
  phoneNumber: z
    .string()
    .min(8, "editAccountDetailsPage.validation.phoneNumber.min")
    .max(9, "editAccountDetailsPage.validation.phoneNumber.max")
    .regex(/^\d*$/, "editAccountDetailsPage.validation.phoneNumber.syntax"),
});

export const resetPasswordSchema = z
  .object({
    password: z
      .string()
      .min(8, "logInPage.validation.password.min")
      .max(32, "logInPage.validation.password.max"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "resetPassword.passwordsValidation.passwordsDoNotMatch",
    path: ["confirmPassword"],
  });

export const changeOwnPasswordSchema = z
  .object({
    oldPassword: z
      .string()
      .min(8, "logInPage.validation.password.min")
      .max(32, "logInPage.validation.password.max"),
    newPassword: z
      .string()
      .min(8, "logInPage.validation.password.min")
      .max(32, "logInPage.validation.password.max"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "resetPassword.passwordsValidation.passwordsDoNotMatch",
    path: ["confirmPassword"],
  });

export const changePasswordByAdminSchema = z
  .object({
    newPassword: z
      .string()
      .min(8, "logInPage.validation.password.min")
      .max(32, "logInPage.validation.password.max"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "resetPassword.passwordsValidation.passwordsDoNotMatch",
    path: ["confirmPassword"],
  });

export const twoFactorCodeSchema = z.object({
  code: z
    .string()
    .min(8, "validation.twoFactorCode")
    .max(8, "validation.twoFactorCode")
    .regex(/[0-9]{8}/, "validation.twoFactorCode"),
});

export const editTariffSchema = z.object({
  coldWaterPrice: z.string().regex(/^\d+(\.\d{2})?$/, "validation.waterPrice"),
  hotWaterPrice: z.string().regex(/^\d+(\.\d{2})?$/, "validation.waterPrice"),
  trashPrice: z.string().regex(/^\d+(\.\d{2})?$/, "validation.waterPrice"),
});

export const editInvoiceSchema = z.object({
  invoiceNumber: z
    .string()
    .regex(/^FV \d{4}\/\d{2}\/\d{2}$/, "validation.invoiceNumber"),
  waterUsage: z.string().regex(/^\d+(\.\d{3})?$/, "validation.waterUsage"),
});

export const addTariffSchema = z.object({
  coldWaterPrice: z.string().regex(/^\d+(\.\d{2})?$/, "validation.waterPrice"),
  hotWaterPrice: z.string().regex(/^\d+(\.\d{2})?$/, "validation.waterPrice"),
  trashPrice: z.string().regex(/^\d+(\.\d{2})?$/, "validation.waterPrice"),
});

export const addInvoiceSchema = z.object({
  invoiceNumber: z
    .string()
    .regex(/^FV \d{4}\/\d{2}\/\d{2}$/, "validation.invoiceNumber"),
  waterUsage: z.string().regex(/^\d+(\.\d{3})?$/, "validation.waterUsage"),
});

export const assignWaterMeterToApartmentSchema = z.object({
  serialNumber: z
    .string()
    .min(1, "validation.serialNumber.min")
    .max(50, "validation.serialNumber.max"),
  startingValue: z
    .string()
    .regex(/^\d+(\.\d{3})?$/, "validation.startingValue"),
  type: z.string().regex(/^(HOT_WATER|COLD_WATER)$/, "validation.type"),
  expectedMonthlyUsage: z
    .string()
    .regex(/^\d+(\.\d{3})?$/, "validation.expectedMonthlyUsage"),
});

export const editWaterMeterSchema = z.object({
  serialNumber: z
    .string()
    .min(1, "validation.serialNumber.min")
    .max(50, "validation.serialNumber.max"),
  expectedMonthlyUsage: z
    .string()
    .regex(/^$|\d+(\.\d{3})?$/, "validation.expectedMonthlyUsage"),
  startingValue: z
    .string()
    .regex(/^\d+(\.\d{3})?$/, "validation.startingValue"),
});

export const addWaterMeterSchema = z.object({
  serialNumber: z
    .string()
    .min(1, "validation.serialNumber.min")
    .max(50, "validation.serialNumber.max"),
  startingValue: z
    .string()
    .regex(/^\d+(\.\d{3})?$/, "validation.startingValue"),
});

export const changeOwnerSchemaType = z.object({
  expectedUsage: z.string().regex(/^\d+(\.\d{3})?$/, "gówno"),
});

export type ChangeOwnerSchema = z.infer<typeof changeOwnerSchemaType>;

export type ChangePasswordByAdminSchema = z.infer<
  typeof changePasswordByAdminSchema
>;

export type ChangeOwnPasswordSchemaType = z.infer<
  typeof changeOwnPasswordSchema
>;

export type AccountDetailsSchemaType = z.infer<typeof accountDetailsSchema>;

export type CreateApartmentSchemaType = z.infer<typeof createApartmentSchema>;

export type EditApartmentSchemaType = z.infer<typeof editApartmentSchema>;

export type EditAccountSchemaType = z.infer<typeof editAccountSchema>;

export type EditAccountDetailsSchemaType = z.infer<
  typeof editAccountDetailsSchema
>;

export type resetPasswordSchemaType = z.infer<typeof resetPasswordSchema>;

export type EditEmailSchemaType = z.infer<typeof editEmailSchema>;

export type LoginSchemaType = z.infer<typeof loginSchema>;

export type resetPasswordEmailSchema = z.infer<typeof resetPasswordEmailSchema>;

export type TwoFactorCodeSchema = z.infer<typeof twoFactorCodeSchema>;

export type EditEmailByAdminSchemaType = z.infer<typeof editEmailByAdminSchema>;

export type EditTariffSchema = z.infer<typeof editTariffSchema>;

export type EditInvoiceSchema = z.infer<typeof editInvoiceSchema>;

export type AddTariffSchema = z.infer<typeof addTariffSchema>;

export type AddInvoiceSchema = z.infer<typeof addInvoiceSchema>;

export type AssignWaterMeterToApartmentSchema = z.infer<
  typeof assignWaterMeterToApartmentSchema
>;

export type EditWaterMeterSchema = z.infer<typeof editWaterMeterSchema>;

export type AddWaterMeterSchema = z.infer<typeof addWaterMeterSchema>;

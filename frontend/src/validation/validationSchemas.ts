import { z } from "zod";

export const editAccountDetailsSchema = z.object({
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
      /\p{L}+(?:-\p{L}+)*/u,
      "editAccountDetailsPage.validation.lastName.syntax"
    ),
  phoneNumber: z
    .string()
    .min(8, "editAccountDetailsPage.validation.phoneNumber.min")
    .max(9, "editAccountDetailsPage.validation.phoneNumber.max")
    .regex(/^\d*$/, "editAccountDetailsPage.validation.phoneNumber.syntax"),
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

export type EditAccountDetailsSchemaType = z.infer<
  typeof editAccountDetailsSchema
>;
export type LoginSchemaType = z.infer<typeof loginSchema>;

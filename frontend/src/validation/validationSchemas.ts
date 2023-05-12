import { z } from "zod";

export const editAccountDetilsSchema = z.object({
  email: z
    .string()
    .min(5, "E-mail cannot be less than 5 characters")
    .max(320, "E-mail cannot be longer than 320 characters")
    .regex(
      new RegExp(
        "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
      ),
      "Invalid e-mail syntax"
    ),
  firstName: z
    .string()
    .min(2, "First name cannot be less than 2 characters")
    .max(50, "First name cannot be longer than 50 characters")
    .regex(new RegExp("^\\p{L}+$", "u"), "First name can contain only letters"),
  lastName: z
    .string()
    .min(2, "Last name cannot be less than 2 characters")
    .max(50, "Last name cannot be longer than 50 characters")
    .regex(
      new RegExp("\\p{L}+(?:-\\p{L}+)*", "u"),
      "Last name can contain only letters and character: -"
    ),
  phoneNumber: z
    .string()
    .min(8, "Phone number cannot be less than 8 characters")
    .max(9, "Phone number cannot be longer than 9 characters")
    .regex(new RegExp("^[0-9]*$"), "Phone number can contain only numbers"),
});

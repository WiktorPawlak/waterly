package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

public class ValidationRegex {

    public static final String EMAIL =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static final String FIRST_NAME = "^\\p{L}+$";

    public static final String LAST_NAME = "\\p{L}+(?:-\\p{L}+)*";

    public static final String PHONE_NUMBER = "^[0-9]*$";
}

package pl.lodz.p.it.ssbd2023.ssbd06.service.security.password;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

@Qualifier
@Target({FIELD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BCryptHash {
}

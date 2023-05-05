package pl.lodz.p.it.ssbd2023.ssbd06.integration.auth;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

class AuthControllerTest extends IntegrationTestsConfig {

    private static final String AUTH_PATH = "/auth";

    @Test
    void whenCredentialsAreCorrectShouldLogInAndReturnJwt() {

        Credentials credentials = new Credentials("admin", "admin12345");

        given()
                .body(credentials)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(OK.getStatusCode())
                .body(notNullValue());
    }

    @ParameterizedTest(name = "Login = {0}, password = {1}")
    @CsvSource({
            "asdawd,niematakiego2",
            "admin,niematakiego",
            "mati,admin12345"
    })
    void whenCredentialsAreIncorrectShouldReturnUnauthorized(String login, String password) {

        Credentials credentials = new Credentials(login, password);

        given()
                .body(credentials)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode())
                .body("message", equalTo("ERROR.AUTHENTICATION"));
    }

    @ParameterizedTest(name = "Login = {0}, password = {1}")
    @CsvSource({
            ",",
            "' ',' '",
            "'',''",
            "mati mati,mati 123",
            "mati,''",
            "'',12345",
    })
    void whenCredentialsHaveIncorrectFormShouldReturnBadRequest(String login, String password) {

        Credentials credentials = new Credentials(login, password);

        given()
                .body(credentials)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("[0].field", notNullValue())
                .body("[0].message", notNullValue());
    }
}
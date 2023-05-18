package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

class AuthControllerTest extends IntegrationTestsConfig {

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
    
    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsNotProvided() {
        given()
                .when()
                .get(ACCOUNT_PATH + "/" + OWNER_ID)
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @ParameterizedTest
    @DisplayName("Should return unauthorized when token")
    @MethodSource("tokensProvider")
    void shouldReturnUnauthorizedWhenTokenIsInvalid(String token) {
        given()
                .header(AUTHORIZATION, token)
                .when()
                .get(ACCOUNT_PATH + "/" + OWNER_ID)
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    private Stream<Arguments> tokensProvider() {
        return Stream.of(
                Arguments.of(Named.of("is empty", "")),
                Arguments.of(Named.of("is invalid", "Bearer cool")),
                Arguments.of(Named.of("is role changed",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                                ".eyJpYXQiOjE2ODM5NzA3MTEsImlzcyI6InNzYmQwNiIsImp0aSI6InRvbWR1dCIsInJvbGVzIjpbIkFETUlOSVNUUkFUT1IiXSwiZXhwIjoxNjgzOTc0MzExfQ" +
                                ".fM14L1rbiuvLd-qqpO3iBQs_kuFLZH_bMeL8LdbGp1c")),
                Arguments.of(Named.of("is expired",
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                                ".eyJpYXQiOjE2ODM1NzIzOTEsImlzcyI6InNzYmQwNiIsImp0aSI6ImFkbWluIiwicm9sZXMiOlsiQURNSU5JU1RSQVRPUiIsIk9XTkVSIiwiRkFDSUxJVFlfTUFOQUdFUiJdLCJleHAiOjE2ODM1NzU5OTF9.sguPNm4U6cRikya3Pd3DQ-f0X0LVBsVHiVmBQbBeolc"))
        );
    }
}
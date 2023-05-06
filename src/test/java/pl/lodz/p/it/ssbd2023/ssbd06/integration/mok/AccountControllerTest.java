package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.restassured.http.Header;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;

class AccountControllerTest extends IntegrationTestsConfig {

    @Test
    void shouldDeactivateActiveAccount() {
        //given
        //TODO check if account is active in database
        given()
                .body(FACILITY_MANAGER_CREDENTIALS)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(OK.getStatusCode())
                .body(notNullValue());

        //when
        given()
                .header(new Header("Authorization", "Bearer " + ADMINISTRATOR_TOKEN))
                .body(DEACTIVATE_ACCOUNT)
                .contentType("application/json-patch+json")
                .when()
                .patch(ACCOUNT_PATH + "/" + FACILITY_MANAGER_ID)
                .then()
                .statusCode(OK.getStatusCode());

        //then
        //TODO check if active status was indeed deactivated in database, when GET is implemented
        given()
                .body(FACILITY_MANAGER_CREDENTIALS)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode())
                .body("message", equalTo("ERROR.AUTHENTICATION"));
    }

    @ParameterizedTest(name = "token = {0}")
    @MethodSource("provideTokensForParameterizedTests")
    void shouldForbidNonAdminUsersToDeactivateAccount(String token) {
        given()
                .header(new Header("Authorization", "Bearer " + token))
                .body(DEACTIVATE_ACCOUNT)
                .contentType("application/json-patch+json")
                .when()
                .patch(ACCOUNT_PATH + "/" + ADMIN_ID)
                .then()
                .statusCode(FORBIDDEN.getStatusCode())
                .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
    }

}

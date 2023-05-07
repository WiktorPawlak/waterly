package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.restassured.http.Header;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.DatabaseConnector;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountWithRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState;

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

    @Test
    void getPagedListTest() {
        //given
        GetPagedAccountListDto getPagedAccountListRequest = new GetPagedAccountListDto(1, 2, "asc", null);

        //when
        List<AccountWithRolesDto> accounts = given()
                .header(new Header("Authorization", "Bearer " + ADMINISTRATOR_TOKEN))
                .body(getPagedAccountListRequest)
                .contentType("application/json")
                .when()
                .post(ACCOUNT_PATH + "/list")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().jsonPath().getList("data", AccountWithRolesDto.class);

        //then
        assertEquals(2, accounts.size());
        assertEquals("admin", accounts.get(0).getLogin());
        assertEquals("new", accounts.get(1).getLogin());

        //given
        getPagedAccountListRequest = new GetPagedAccountListDto(null, null, "asc", null);

        //when
        accounts = given()
                .header(new Header("Authorization", "Bearer " + ADMINISTRATOR_TOKEN))
                .body(getPagedAccountListRequest)
                .contentType("application/json")
                .when()
                .post(ACCOUNT_PATH + "/list")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().jsonPath().getList("data", AccountWithRolesDto.class);

        //then
        assertEquals(3, accounts.size());
    }

    @Test
    @SneakyThrows
    void shouldConfirmRegisteredAccountWhenVerificationTokenCorrect() {
        // given
        DatabaseConnector databaseConnector = new DatabaseConnector(POSTGRES_PORT);
        AccountDto accountDto = new AccountDto();
        accountDto.setPhoneNumber("123123123");
        accountDto.setEmail("test@test.test");
        accountDto.setLogin("test");
        accountDto.setFirstName("Test");
        accountDto.setLastName("Test");
        accountDto.setPassword("p@ssw0rd");
        accountDto.setLanguageTag("en-US");

        // when
        given()
                .body(accountDto)
                .when()
                .post(ACCOUNT_PATH + "/register")
                .then()
                .statusCode(CREATED.getStatusCode());

        // then
        String accountStateBefore = databaseConnector.executeQuery(
                "SELECT account_state FROM account WHERE login = 'test'"
        ).getString("account_state");

        assertEquals(accountStateBefore, AccountState.NOT_CONFIRMED.name());

        // given
        String token = databaseConnector.executeQuery(
                "SELECT token FROM verification_token vt JOIN account ON account_id = vt.account_id WHERE login = 'test'"
        ).getString("token");

        // when
        given()
                .when()
                .put(ACCOUNT_PATH + "/confirm-registration?token=" + token)
                .then()
                .statusCode(OK.getStatusCode());

        //then
        String accountStateAfter = databaseConnector.executeQuery(
                "SELECT account_state FROM account WHERE login = 'test'"
        ).getString("account_state");

        assertEquals(accountStateAfter, AccountState.TO_CONFIRM.name());
    }

    @Test
    void shouldRespondWith404WhenVerificationTokenDoesntExist() {
        given()
                .when()
                .put(ACCOUNT_PATH + "/confirm-registration?token=someRandomTokenThatDoesntExist")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
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

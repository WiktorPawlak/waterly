package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.DatabaseConnector;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountWithRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
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
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
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
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(getPagedAccountListRequest)
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
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(getPagedAccountListRequest)
                .when()
                .post(ACCOUNT_PATH + "/list")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().jsonPath().getList("data", AccountWithRolesDto.class);

        //then
        assertEquals(3, accounts.size());
    }

    @ParameterizedTest(name = "pattern = {0}, expected account ids = {1}")
    @MethodSource("providePatterns")
    void shouldReturnAccountsFilteredByPattern(String pattern, List<Long> expectedIds) {
        //given
        GetPagedAccountListDto getPagedAccountListRequest = new GetPagedAccountListDto(1, 10, "asc", null);

        //when
        List<AccountWithRolesDto> accounts = given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .queryParam("pattern", pattern)
                .body(getPagedAccountListRequest)
                .when()
                .post(ACCOUNT_PATH + "/list")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().jsonPath().getList("data", AccountWithRolesDto.class);

        //then
        assertEquals(expectedIds, accounts.stream().map(AccountWithRolesDto::getId).toList());
    }

    @Test
    @SneakyThrows
    void shouldConfirmRegisteredAccountWhenVerificationTokenCorrect() {
        DatabaseConnector databaseConnector = new DatabaseConnector(POSTGRES_PORT);
        // given
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

    @Test
    void shouldEditSelfAccountDetailsWhenEmailNotChanged() {
        String firstName = "Kamil";
        String lastName = "Kowalski-Nowak";
        String phoneNumber = "000000000";

        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(getOwnerAccount().getEmail(), firstName, lastName, phoneNumber);

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(dto)
                .when()
                .put(ACCOUNT_PATH + "/self")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        assertEquals(firstName, getOwnerAccount().getFirstName());
        assertEquals(lastName, getOwnerAccount().getLastName());
        assertEquals(phoneNumber, getOwnerAccount().getPhoneNumber());
    }

    @ParameterizedTest
    @SneakyThrows
    @CsvSource({"/self", "/1"})
    void shouldEditAccountDetailsWithChangedEmailWhenEditAccepted(String path) {
        DatabaseConnector databaseConnector = new DatabaseConnector(POSTGRES_PORT);
        String firstName = "Kamil";
        String lastName = "Kowalski-Nowak";
        String phoneNumber = "000000000";
        String email = "mati@mati.com";

        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(email, firstName, lastName, phoneNumber);

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(dto)
                .when()
                .put(ACCOUNT_PATH + path)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        assertNotEquals(firstName, getAdministratorAccount().getFirstName());
        assertNotEquals(lastName, getAdministratorAccount().getLastName());
        assertNotEquals(phoneNumber, getAdministratorAccount().getPhoneNumber());
        assertNotEquals(email, getAdministratorAccount().getEmail());

        String token = databaseConnector.executeQuery(
                "SELECT token FROM verification_token WHERE account_id = " + getAdministratorAccount().getId()
        ).getString("token");

        // when
        given()
                .when()
                .post(ACCOUNT_PATH + "/account-details/accept?token=" + token)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        assertEquals(firstName, getAdministratorAccount().getFirstName());
        assertEquals(lastName, getAdministratorAccount().getLastName());
        assertEquals(phoneNumber, getAdministratorAccount().getPhoneNumber());
        assertEquals(email, getAdministratorAccount().getEmail());
    }

    @ParameterizedTest
    @CsvSource({
            ",,,,",
            "' ',' ',' ',' '",
            "'','','',''",
            "mati mati,mati 123,mati mati,mati mati",
    })
    void whenUpdateSelfAccountDetailsAndDataIsIncorrectShouldReturnBadRequest(String email, String firstName, String lastName, String phoneNumber) {
        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(email, firstName, lastName, phoneNumber);

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(dto)
                .when()
                .put(ACCOUNT_PATH + "/self")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("[0].field", notNullValue())
                .body("[0].message", notNullValue());
    }

    @ParameterizedTest
    @CsvSource({"/self", "/1"})
    void whenUpdateAccountDetailsAndAccountWithEmailOrPhoneNumberExistShouldReturnConflict(String path) {
        AccountDto adminAccount = getAdministratorAccount();
        UpdateAccountDetailsDto dtoWithExistedEmail =
                new UpdateAccountDetailsDto(getOwnerAccount().getEmail(),
                        adminAccount.getFirstName(),
                        adminAccount.getLastName(),
                        adminAccount.getPhoneNumber()
                );

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(dtoWithExistedEmail)
                .when()
                .put(ACCOUNT_PATH + path)
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR.ACCOUNT_WITH_EMAIL_EXIST"));

        UpdateAccountDetailsDto dtoWithExistedPhoneNumber =
                new UpdateAccountDetailsDto(adminAccount.getEmail(),
                        adminAccount.getFirstName(),
                        adminAccount.getLastName(),
                        getFacilityManagerAccount().getPhoneNumber()
                );

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(dtoWithExistedPhoneNumber)
                .when()
                .put(ACCOUNT_PATH + "/self")
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR.ACCOUNT_WITH_PHONE_NUMBER_EXIST"));
    }

    @Test
    void shouldEditOtherAccountAccountDetailsWhenEmailNotChanged() {
        String firstName = "Kamil";
        String lastName = "Kowalski-Nowak";
        String phoneNumber = "000000000";

        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(getOwnerAccount().getEmail(), firstName, lastName, phoneNumber);

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(dto)
                .when()
                .put(ACCOUNT_PATH + "/" + OWNER_ID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        assertEquals(firstName, getOwnerAccount().getFirstName());
        assertEquals(lastName, getOwnerAccount().getLastName());
        assertEquals(phoneNumber, getOwnerAccount().getPhoneNumber());
    }

    @ParameterizedTest
    @MethodSource("provideTokensForParameterizedTests")
    void shouldForbidNonAdminUsersToUpdateOtherAccountsAccountDetails(String token) {
        String firstName = "Kamil";
        String lastName = "Kowalski-Nowak";
        String phoneNumber = "000000000";
        String email = "mati@mati.com";

        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(email, firstName, lastName, phoneNumber);

        given()
                .header(AUTHORIZATION, token)
                .body(dto)
                .when()
                .put(ACCOUNT_PATH + "/" + ADMIN_ID)
                .then()
                .statusCode(FORBIDDEN.getStatusCode())
                .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
    }

    @ParameterizedTest
    @MethodSource("provideTokensForParameterizedTests")
    void shouldForbidNonAdminUsersToDeactivateAccount(String token) {
        given()
                .header(AUTHORIZATION, token)
                .body(DEACTIVATE_ACCOUNT)
                .contentType("application/json-patch+json")
                .when()
                .patch(ACCOUNT_PATH + "/" + ADMIN_ID)
                .then()
                .statusCode(FORBIDDEN.getStatusCode())
                .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
    }

    private Stream<Arguments> providePatterns() {
        return Stream.of(
                Arguments.of("konto ", List.of(1L, 2L)),
                Arguments.of(" new", List.of(2L)),
                Arguments.of("mateusz Strz", List.of(1L)),
                Arguments.of("StrZelecki matEu", List.of(1L)),
                Arguments.of(null, List.of(1L, 2L, 3L)),
                Arguments.of(" ", List.of(1L, 2L, 3L))
        );
    }

}

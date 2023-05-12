package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import io.vavr.Tuple2;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.DatabaseConnector;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountWithRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordChangeByAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

class AccountControllerTest extends IntegrationTestsConfig {

    @Test
    void registerUserWithViolatedConstraintsShouldFail() {

        //given
        CreateAccountDto accountDto = prepareCreateAccountDto();
        accountDto.setLogin("new");

        // when && then
        given()
                .body(accountDto)
                .when()
                .post(ACCOUNT_PATH + "/register")
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR.ACCOUNT_WITH_LOGIN_EXIST"));

        //given
        accountDto = prepareCreateAccountDto();
        accountDto.setEmail("tomdut@gmail.com");

        // when && then
        given()
                .body(accountDto)
                .when()
                .post(ACCOUNT_PATH + "/register")
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR.ACCOUNT_WITH_EMAIL_EXIST"));

        //given
        accountDto = prepareCreateAccountDto();
        accountDto.setPhoneNumber("123456789");

        // when && then
        given()
                .body(accountDto)
                .when()
                .post(ACCOUNT_PATH + "/register")
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR.ACCOUNT_WITH_PHONE_NUMBER_EXIST"));

    }

    @Test
    @SneakyThrows
    void shouldDeactivateActiveAccount() {
        DatabaseConnector databaseConnector = new DatabaseConnector(POSTGRES_PORT);

        //given
        boolean active = databaseConnector.executeQuery(
                "SELECT active FROM account WHERE login = 'tomdut'"
        ).getBoolean("active");
        assertTrue(active);
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
        boolean inactive = !databaseConnector.executeQuery(
                "SELECT active FROM account WHERE login = 'tomdut'"
        ).getBoolean("active");
        assertTrue(inactive);
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
        assertEquals(4, accounts.size());
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
        CreateAccountDto accountDto = prepareCreateAccountDto();

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
        String languageTag = "pl-PL";

        Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(ownerAccount._1.getId(),
                ownerAccount._1.getEmail(),
                firstName, lastName, phoneNumber,
                languageTag,
                ownerAccount._1.getVersion());

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(dto)
                .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
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
        String languageTag = "pl-PL";

        Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(ownerAccount._1.getId(),
                email,
                firstName,
                lastName,
                phoneNumber,
                languageTag,
                ownerAccount._1.getVersion());

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(dto)
                .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
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
    void whenUpdateSelfAccountDetailsAndDataIsIncorrectShouldReturnBadRequest(String firstName, String lastName, String phoneNumber, String languageTag) {
        Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

        UpdateAccountDetailsDto dto =
                new UpdateAccountDetailsDto(ownerAccount._1.getId(),
                        ownerAccount._1.getEmail(),
                        firstName,
                        lastName,
                        phoneNumber,
                        languageTag,
                        ownerAccount._1.getVersion());

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(dto)
                .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
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
        Tuple2<AccountDto, String> adminAccount = getAdministratorAccountWithEtag();
        Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
        UpdateAccountDetailsDto dtoWithExistedEmail =
                new UpdateAccountDetailsDto(adminAccount._1.getId(),
                        ownerAccount._1.getEmail(),
                        adminAccount._1.getFirstName(),
                        adminAccount._1.getLastName(),
                        adminAccount._1.getPhoneNumber(),
                        adminAccount._1.getLanguageTag(),
                        adminAccount._1.getVersion()
                );

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(dtoWithExistedEmail)
                .header(IF_MATCH_HEADER_NAME, adminAccount._2)
                .when()
                .put(ACCOUNT_PATH + path)
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR.ACCOUNT_WITH_EMAIL_EXIST"));

        UpdateAccountDetailsDto dtoWithExistedPhoneNumber =
                new UpdateAccountDetailsDto(ownerAccount._1.getId(),
                        ownerAccount._1.getEmail(),
                        ownerAccount._1.getFirstName(),
                        ownerAccount._1.getLastName(),
                        getFacilityManagerAccount().getPhoneNumber(),
                        ownerAccount._1().getLanguageTag(),
                        ownerAccount._1.getVersion()
                );

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(dtoWithExistedPhoneNumber)
                .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
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
        String languageTag = "pl-PL";

        Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

        UpdateAccountDetailsDto dto = new UpdateAccountDetailsDto(ownerAccount._1.getId(),
                ownerAccount._1.getEmail(),
                firstName,
                lastName,
                phoneNumber,
                languageTag,
                ownerAccount._1.getVersion());

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(dto)
                .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
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
        String languageTag = "pl-PL";


        Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

        UpdateAccountDetailsDto dto =
                new UpdateAccountDetailsDto(ownerAccount._1.getId(),
                        ownerAccount._1.getEmail(),
                        firstName,
                        lastName,
                        phoneNumber,
                        languageTag,
                        ownerAccount._1.getVersion());

        given()
                .header(AUTHORIZATION, token)
                .body(dto)
                .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
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

    @Test
    @SneakyThrows
    void shouldChangePasswordRequestProperlyAfterUserChange() {
        DatabaseConnector databaseConnector = new DatabaseConnector(POSTGRES_PORT);
        String newPassword = "123jantes";
        PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto(newPassword);
        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .queryParam("email", getOwnerAccount().getEmail())
                .body(passwordChangeByAdminDto)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(ACCOUNT_PATH + "/password/request-change")
                .then()
                .statusCode(OK.getStatusCode());

        String changePasswordToken = databaseConnector.executeQuery(
                "SELECT token FROM verification_token WHERE account_id = " + getOwnerAccount().getId()
        ).getString("token");

        String newPassword1 = "1234jantes";
        PasswordResetDto changePasswordRequestDto = new PasswordResetDto(changePasswordToken, newPassword1, TokenType.CHANGE_PASSWORD);
        given()
                //.header(AUTHORIZATION, OWNER_TOKEN)
                .body(changePasswordRequestDto)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(ACCOUNT_PATH + "/password/reset")
                .then()
                .statusCode(OK.getStatusCode());

        given()
                .body(new Credentials("new", "1234jantes"))
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(OK.getStatusCode());

    }

    @Test
    @SneakyThrows
    void shouldChangePasswordRequestProperlyAfterAdminChange() {
        String newPassword = "123jantes";
        PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto(newPassword);
        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .queryParam("email", getOwnerAccount().getEmail())
                .body(passwordChangeByAdminDto)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(ACCOUNT_PATH + "/password/request-change")
                .then()
                .statusCode(OK.getStatusCode());

        given()
                .body(new Credentials("new", "123jantes"))
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(AUTH_PATH + "/login")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideTokensForParameterizedTests")
    void shouldForbidNotAdminUsersToRequestPasswordChange(String token) {
        PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto("123jantes");
        given()
                .header(AUTHORIZATION, token)
                .queryParam("email", getOwnerAccount().getEmail())
                .body(passwordChangeByAdminDto)
                .when()
                .post(ACCOUNT_PATH + "/password/request-change")
                .then()
                .statusCode(FORBIDDEN.getStatusCode())
                .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
    }

    @Test
    @SneakyThrows
    void noMatchingEmailsForPasswordChangeRequest() {
        String email = "tenEmailNieIstnieje@aaa.com";
        PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto("123jantes");
        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .queryParam("email", email)
                .body(passwordChangeByAdminDto)
                .when()
                .post(ACCOUNT_PATH + "/password/request-change")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("message", equalTo("ERROR.NO_MATCHING_EMAILS"));
    }

    @Test
    void shouldRespondWith404WhenChangePasswordTokenDoesntExist() {
        String newPassword = "123jantes";
        String thisTokenIsNotCorrect = "11111111-1111-1111-1111-a4cbafae584d";
        PasswordResetDto changePasswordRequestDto = new PasswordResetDto(thisTokenIsNotCorrect, newPassword, TokenType.CHANGE_PASSWORD);

        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(changePasswordRequestDto)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(ACCOUNT_PATH + "/password/reset")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .body("message", equalTo("ERROR.TOKEN_NOT_FOUND"));
    }

    @Test
    @SneakyThrows
    void shouldRespondWith404WhenChangePasswordTokenWasAlreadyUsed() {
        DatabaseConnector databaseConnector = new DatabaseConnector(POSTGRES_PORT);
        PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto("123jantes");
        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .queryParam("email", getOwnerAccount().getEmail())
                .body(passwordChangeByAdminDto)
                .when()
                .post(ACCOUNT_PATH + "/password/request-change")
                .then()
                .statusCode(OK.getStatusCode());

        String changePasswordToken = databaseConnector.executeQuery(
                "SELECT token FROM verification_token WHERE account_id = " + getOwnerAccount().getId()
        ).getString("token");

        String newPassword = "124jantes";
        PasswordResetDto changePasswordRequestDto = new PasswordResetDto(changePasswordToken, newPassword, TokenType.CHANGE_PASSWORD);
        given()
                //.header(AUTHORIZATION, OWNER_TOKEN)
                .body(changePasswordRequestDto)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(ACCOUNT_PATH + "/password/reset")
                .then()
                .statusCode(OK.getStatusCode());

        String newPassword1 = "125jantes";
        PasswordResetDto newChangePasswordRequestDto = new PasswordResetDto(changePasswordToken, newPassword1, TokenType.CHANGE_PASSWORD);
        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(newChangePasswordRequestDto)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(ACCOUNT_PATH + "/password/reset")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .body("message", equalTo("ERROR.TOKEN_NOT_FOUND"));
    }

    @Test
    void shouldRejectOwnerAccountSuccessfully() {
        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .when()
                .delete(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID + "/reject")
                .then()
                .statusCode(OK.getStatusCode());

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .when()
                .get(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldForbidNotFacilityManagerUsersRejectOwnerAccount() {
        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .when()
                .delete(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID + "/reject")
                .then()
                .statusCode(FORBIDDEN.getStatusCode())
                .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
    }

    @Test
    void shouldFailRejectOwnerAccountWhenAccountStatusIsDifferentThanToConfirm() {
        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .when()
                .delete(ACCOUNT_PATH + "/" + OWNER_ID + "/reject")
                .then()
                .statusCode(CONFLICT.getStatusCode())
                .body("message", equalTo("ERROR_ACCOUNT_NOT_WAITING_FOR_CONFIRMATION"));
    }

    @Test
    void shouldFailRejectOwnerAccountWhenAccountDoesNotExist() {
        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .when()
                .delete(ACCOUNT_PATH + "/99999999999/reject")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .body("message", equalTo("ERROR.RESOURCE_NOT_FOUND"));
    }

    private Stream<Arguments> providePatterns() {
        return Stream.of(
                Arguments.of("konto ", List.of(1L, 2L)),
                Arguments.of(" new", List.of(2L)),
                Arguments.of("mateusz Strz", List.of(1L)),
                Arguments.of("StrZelecki matEu", List.of(1L)),
                Arguments.of(null, List.of(1L, 2L, 4L, 3L)),
                Arguments.of(" ", List.of(1L, 2L, 4L, 3L))
        );
    }

    private static CreateAccountDto prepareCreateAccountDto() {
        CreateAccountDto accountDto = new CreateAccountDto();
        accountDto.setPhoneNumber("123123123");
        accountDto.setEmail("test@test.test");
        accountDto.setLogin("test");
        accountDto.setFirstName("Test");
        accountDto.setLastName("Test");
        accountDto.setPassword("p@ssw0rd");
        accountDto.setLanguageTag("en-US");
        return accountDto;
    }
}

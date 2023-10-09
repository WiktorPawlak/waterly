package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.vavr.Tuple2;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.MariaDBTestResource;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.ListAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordChangeByAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

@QuarkusTest
@QuarkusTestResource(value = MariaDBTestResource.class, restrictToAnnotatedClass = true)
@Order(4)
public class AccountControllerTest2BecauseOfMaxConnectionPool extends IntegrationTestsConfig {
    @Nested
    class EditAccountDetails {

        @ParameterizedTest
        @SneakyThrows
        @CsvSource({"/self/email", "/1/email"})
        void shouldEditEmailWhenEditAccepted(String path) {
            String email = "mati@mati.com";

            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
            EditEmailDto dto = new EditEmailDto(ownerAccount._1.getId(), email, ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dto)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + path)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            assertNotEquals(email, getAdministratorAccount().getEmail());

            String token = databaseConnector.executeQuery(
                    "SELECT token FROM verification_token WHERE account_id = " + getAdministratorAccount().getId()
            ).getString("token");

            // when
            given()
                    .when()
                    .post(ACCOUNT_PATH + "/email/accept?token=" + token)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            assertEquals(email, getAdministratorAccount().getEmail());
        }

        @ParameterizedTest
        @SneakyThrows
        @CsvSource({"/self/email", "/1/email"})
        void WhenEditEmailAndEditAlreadyAcceptedShouldReturnNotFound(String path) {
            String email = "mati@mati.com";

            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
            EditEmailDto dto = new EditEmailDto(ownerAccount._1.getId(), email, ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dto)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + path)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            String token = databaseConnector.executeQuery(
                    "SELECT token FROM verification_token WHERE account_id = " + getAdministratorAccount().getId()
            ).getString("token");

            given()
                    .when()
                    .post(ACCOUNT_PATH + "/email/accept?token=" + token)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            given()
                    .when()
                    .post(ACCOUNT_PATH + "/email/accept?token=" + token)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }


        @ParameterizedTest
        @CsvSource({"/self/email", "/1/email"})
        void whenUpdateEmailAndEmailExistShouldReturnConflict(String path) {
            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
            EditEmailDto dtoWithExistedEmail = new EditEmailDto(ownerAccount._1.getId(), ownerAccount._1.getEmail(), ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dtoWithExistedEmail)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + path)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.ACCOUNT_WITH_EMAIL_EXIST"));
        }

        @Test
        void whenUpdateEmailAndEmailExistInWaitingEmailShouldReturnConflict() {
            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
            EditEmailDto dtoEmail = new EditEmailDto(ownerAccount._1.getId(), ownerAccount._1.getEmail(), ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dtoEmail)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + "/" + OWNER_ID + "/email")
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dtoEmail)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + "/self/email")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.ACCOUNT_WITH_EMAIL_EXIST"));
        }


        @ParameterizedTest
        @CsvSource({"/self", "/1"})
        void whenEditAccountDetailsAndPhoneNumberExistShouldReturnConflict(String path) {
            Tuple2<AccountDto, String> adminAccount = getAdministratorAccountWithEtag();

            EditAccountDetailsDto dtoWithExistedPhoneNumber =
                    new EditAccountDetailsDto(adminAccount._1.getId(),
                            adminAccount._1.getFirstName(),
                            adminAccount._1.getLastName(),
                            getFacilityManagerAccount().getPhoneNumber(),
                            adminAccount._1().getLanguageTag(),
                            adminAccount._1.getVersion()
                    );

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dtoWithExistedPhoneNumber)
                    .header(IF_MATCH_HEADER_NAME, adminAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + path)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.ACCOUNT_WITH_PHONE_NUMBER_EXIST"));
        }

    }

    @Nested
    class ConfirmRegistration {

        @Test
        void shouldRespondWith404WhenVerificationTokenDoesntExist() {
            given()
                    .when()
                    .put(ACCOUNT_PATH + "/confirm-registration?token=someRandomTokenThatDoesntExist")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }
    }

    @Nested
    class ChangeOwnPassword {
        @Test
        void shouldChangeOwnPassword() {
            given()
                    .body(new Credentials("admin", "admin12345"))
                    .when()
                    .post(AUTH_PATH + "/login")
                    .then()
                    .statusCode(OK.getStatusCode());

            AccountPasswordDto passwordDto = new AccountPasswordDto("admin12345", "newPassword");
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(passwordDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .put(ACCOUNT_PATH + "/self/password")
                    .then()
                    .statusCode(OK.getStatusCode());

            given()
                    .body(new Credentials("admin", "newPassword"))
                    .when()
                    .post(AUTH_PATH + "/login")
                    .then()
                    .statusCode(OK.getStatusCode());
        }

        @Test
        void shouldFailChangeOwnPasswordWhenOldPasswordDoesNotMatch() {
            AccountPasswordDto passwordDto = new AccountPasswordDto("12345admin", "newPassword");
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(passwordDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .put(ACCOUNT_PATH + "/self/password")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("message", equalTo("ERROR.NOT_MATCHING_PASSWORDS"));
        }

        @Test
        void shouldFailChangeOwnPasswordWhenNewPasswordIsTheSameAsOld() {
            AccountPasswordDto passwordDto = new AccountPasswordDto("admin12345", "admin12345");
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(passwordDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .put(ACCOUNT_PATH + "/self/password")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.IDENTICAL_PASSWORDS"));
        }

        @Test
        void shouldFailChangeOwnPasswordWhenNewPasswordIsNotValid() {
            AccountPasswordDto invalidPasswordDto = new AccountPasswordDto("admin12345", "X");
            String test = given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(invalidPasswordDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .put(ACCOUNT_PATH + "/self/password")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .extract().jsonPath().getString("message");
            assertEquals(test, "[size must be between 8 and 32]");
        }
    }

    @Nested
    class ChangeOtherAccountPassword {
        @Test
        @SneakyThrows
        void shouldChangePasswordRequestProperlyAfterUserChange() {
            String newPassword = "123jantes";
            PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto(newPassword);
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

            String newPassword1 = "1234jantes";
            PasswordResetDto changePasswordRequestDto = new PasswordResetDto(changePasswordToken, newPassword1);
            given()
                    .body(changePasswordRequestDto)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            given()
                    .body(new Credentials("new", "1234jantes"))
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
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNotAdminUsersToRequestPasswordChange(String token) {
            PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto("123jantes");
            given()
                    .header(AUTHORIZATION, token)
                    .queryParam("email", getOwnerAccount().getEmail())
                    .body(passwordChangeByAdminDto)
                    .when()
                    .post(ACCOUNT_PATH + "/password/request-change")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        @SneakyThrows
        void noMatchingEmailsForPasswordChangeRequest() {
            String email = "thisEmailDoesNotExist@aaa.com";
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
        @SneakyThrows
        void shouldFailChangePasswordByAdminWhenNewPasswordIsNotValid() {
            String newPassword = "123jantes";
            PasswordChangeByAdminDto passwordChangeByAdminDto = new PasswordChangeByAdminDto(newPassword);
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

            PasswordResetDto changePasswordRequestDto = new PasswordResetDto(changePasswordToken, "X");
            String test = given()
                    .body(changePasswordRequestDto)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .extract().jsonPath().getString("message");

            assertEquals("[size must be between 8 and 32]", test);
        }

        @Test
        void shouldRespondWith404WhenChangePasswordTokenDoesntExist() {
            String newPassword = "123jantes";
            String thisTokenIsNotCorrect = "11111111-1111-1111-1111-a4cbafae584d";
            PasswordResetDto changePasswordRequestDto = new PasswordResetDto(thisTokenIsNotCorrect, newPassword);

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
            PasswordResetDto changePasswordRequestDto = new PasswordResetDto(changePasswordToken, newPassword);
            given()
                    //.header(AUTHORIZATION, OWNER_TOKEN)
                    .body(changePasswordRequestDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            String newPassword1 = "125jantes";
            PasswordResetDto newChangePasswordRequestDto = new PasswordResetDto(changePasswordToken, newPassword1);
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
        void shouldFailChangeOwnPasswordWhenNewPasswordIsNotValid() {
            AccountPasswordDto invalidPasswordDto = new AccountPasswordDto("admin12345", "X");
            String test = given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(invalidPasswordDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .put(ACCOUNT_PATH + "/self/password")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .extract().jsonPath().getString("message");
            assertEquals("[size must be between 8 and 32]", test);
        }
    }

    @Nested
    class ResetPassword {
        @Test
        @SneakyThrows
        void shouldResetPasswordProperly() {
            given()
                    .queryParam("email", getOwnerAccount().getEmail())
                    .when()
                    .post(ACCOUNT_PATH + "/password/request-reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            String resetPasswordToken = databaseConnector.executeQuery(
                    "SELECT token FROM verification_token WHERE account_id = " + getOwnerAccount().getId()
            ).getString("token");

            PasswordResetDto resetDto = new PasswordResetDto(resetPasswordToken, "resetedPassword");

            given()
                    .body(resetDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            given()
                    .body(new Credentials("new", "resetedPassword"))
                    .when()
                    .post(AUTH_PATH + "/login")
                    .then()
                    .statusCode(OK.getStatusCode());
        }

        @Test
        @SneakyThrows
        void shouldFailPasswordResetWhenNoMatchingEmailsForPasswordResetRequest() {
            given()
                    .queryParam("email", "thisEmailDoesNotExist@aaa.com")
                    .when()
                    .post(ACCOUNT_PATH + "/password/request-reset")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("message", equalTo("ERROR.NO_MATCHING_EMAILS"));
        }

        @Test
        void shouldRespondWith404WhenResetPasswordTokenDoesntExist() {
            String wrongToken = "11111111-1111-1111-1111-a4cbafae584d";
            PasswordResetDto resetDto = new PasswordResetDto(wrongToken, "123jantes");

            given()
                    .body(resetDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode())
                    .body("message", equalTo("ERROR.TOKEN_NOT_FOUND"));
        }

        @Test
        @SneakyThrows
        void shouldRespondWith404WhenResetPasswordTokenWasAlreadyUsed() {
            given()
                    .queryParam("email", getOwnerAccount().getEmail())
                    .when()
                    .post(ACCOUNT_PATH + "/password/request-reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            String resetPasswordToken = databaseConnector.executeQuery(
                    "SELECT token FROM verification_token WHERE account_id = " + getOwnerAccount().getId()
            ).getString("token");

            PasswordResetDto resetDto = new PasswordResetDto(resetPasswordToken, "resetedPassword");

            given()
                    .body(resetDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            PasswordResetDto newResetDto = new PasswordResetDto(resetPasswordToken, "newResetedPassword");

            given()
                    .body(newResetDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode())
                    .body("message", equalTo("ERROR.TOKEN_NOT_FOUND"));
        }

        @Test
        @SneakyThrows
        void shouldFailPasswordResetWhenPasswordIsNotValid() {
            given()
                    .queryParam("email", getOwnerAccount().getEmail())
                    .when()
                    .post(ACCOUNT_PATH + "/password/request-reset")
                    .then()
                    .statusCode(OK.getStatusCode());

            String resetPasswordToken = databaseConnector.executeQuery(
                    "SELECT token FROM verification_token WHERE account_id = " + getOwnerAccount().getId()
            ).getString("token");

            PasswordResetDto resetDto = new PasswordResetDto(resetPasswordToken, "X");

            String test = given()
                    .body(resetDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .when()
                    .post(ACCOUNT_PATH + "/password/reset")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .extract().jsonPath().getString("message");

            assertEquals("[size must be between 8 and 32]", test);
        }
    }

    @Nested
    class AcceptOwnerAccount {
        @Test
        @SneakyThrows
        void shouldAcceptOwnerAccountSuccessfully() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .post(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID + "/accept")
                    .then()
                    .statusCode(OK.getStatusCode());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .when()
                    .get(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID)
                    .then()
                    .log().all()
                    .statusCode(OK.getStatusCode())
                    .body("accountState", equalTo("CONFIRMED"));

            String accountState =
                    databaseConnector.executeQuery("SELECT account_state FROM account WHERE id = '" + NOT_CONFIRMED_OWNER_ID + "'").getString("account_state");
            assertEquals("CONFIRMED", accountState);
        }

        @Test
        void shouldFailAcceptOwnerAccountWhenAccountStatusIsDifferentThanToConfirm() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .post(ACCOUNT_PATH + "/" + OWNER_ID + "/accept")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR_ACCOUNT_NOT_WAITING_FOR_CONFIRMATION"));
        }

        @Test
        void shouldFailAcceptOwnerAccountWhenUserIsNotAuthorized() {
            given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .when()
                    .post(ACCOUNT_PATH + "/" + OWNER_ID + "/accept")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldFailAcceptWhenOwnerAccountNotExist() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .post(ACCOUNT_PATH + "/" + NONE_EXISTENT_ACCOUNT_ID + "/accept")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode())
                    .body("message", equalTo("ERROR.RESOURCE_NOT_FOUND"));
        }

        @Test
        void shouldAcceptOnlyOnceWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            int threadNumber = 10;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            List<Integer> responseCodes = new ArrayList<>();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    Response response = given()
                            .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                            .when()
                            .post(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID + "/accept")
                            .then()
                            .extract().response();
                    int responseCode = response.getStatusCode();
                    responseCodes.add(responseCode);
                    numberFinished.getAndIncrement();
                }));
            }

            threads.forEach(Thread::start);
            cyclicBarrier.await();
            while (numberFinished.get() != threadNumber) {
            }
            assertEquals(1, responseCodes.stream().filter(responseCode -> responseCode == OK.getStatusCode()).toList().size());
        }
    }

    @Nested
    class RejectOwnerAccount {
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
        void shouldFailRejectOwnerAccountWhenUserIsNotAuthorized() {
            given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .when()
                    .delete(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID + "/reject")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
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
        void shouldFailRejectOwnerAccountWhenAccountNotExist() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .delete(ACCOUNT_PATH + "/99999999999/reject")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode())
                    .body("message", equalTo("ERROR.RESOURCE_NOT_FOUND"));
        }

        @Test
        void shouldAcceptOnlyOnceWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            int threadNumber = 10;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            List<Integer> responseCodes = new ArrayList<>();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    Response response = given()
                            .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                            .when()
                            .delete(ACCOUNT_PATH + "/" + NOT_CONFIRMED_OWNER_ID + "/reject")
                            .then()
                            .extract().response();
                    int responseCode = response.getStatusCode();
                    responseCodes.add(responseCode);
                    numberFinished.getAndIncrement();
                }));
            }

            threads.forEach(Thread::start);
            cyclicBarrier.await();
            while (numberFinished.get() != threadNumber) {
            }
            assertEquals(1, responseCodes.stream().filter(responseCode -> responseCode == OK.getStatusCode()).toList().size());
        }
    }

    @Nested
    class PagedList {
        @Test
        void getPagedListTest() {
            //given
            GetPagedAccountListDto getPagedAccountListRequest = new GetPagedAccountListDto(1, 2, "asc", null);

            //when
            List<ListAccountDto> accounts = given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(getPagedAccountListRequest)
                    .when()
                    .post(ACCOUNT_PATH + "/list")
                    .then()
                    .statusCode(OK.getStatusCode())
                    .extract().body().jsonPath().getList("data", ListAccountDto.class);

            //then
            assertEquals(2, accounts.size());
            assertEquals("admin", accounts.get(0).getLogin());
            assertEquals("jerzy", accounts.get(1).getLogin());

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
                    .extract().body().jsonPath().getList("data", ListAccountDto.class);

            //then
            assertEquals(5, accounts.size());
        }

        @ParameterizedTest(name = "pattern = {0}, expected account ids = {1}")
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#providePatterns")
        void shouldReturnAccountsFilteredByPattern(String pattern, List<Long> expectedIds) {
            //given
            GetPagedAccountListDto getPagedAccountListRequest = new GetPagedAccountListDto(1, 10, "asc", null);

            //when
            List<ListAccountDto> accounts = given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .queryParam("pattern", pattern)
                    .body(getPagedAccountListRequest)
                    .when()
                    .post(ACCOUNT_PATH + "/list")
                    .then()
                    .statusCode(OK.getStatusCode())
                    .extract().body().jsonPath().getList("data", ListAccountDto.class);

            //then
            assertEquals(expectedIds, accounts.stream().map(ListAccountDto::getId).toList());
        }
    }

    private static Stream<Arguments> providePatterns() {
        return Stream.of(
                Arguments.of("konto ", List.of(1L, 2L)),
                Arguments.of(" new", List.of(2L)),
                Arguments.of("mateusz Strz", List.of(1L)),
                Arguments.of("StrZelecki matEu", List.of(1L)),
                Arguments.of(null, List.of(1L, 5L, 2L, 4L, 3L)),
                Arguments.of(" ", List.of(1L, 5L, 2L, 4L, 3L))
        );
    }

    protected static CreateAccountDto prepareCreateAccountDto() {
        return CreateAccountDto.builder()
                .phoneNumber("123123123")
                .email("test@test.test")
                .login("test")
                .firstName("Test")
                .lastName("Test")
                .password("p@ssw0rd")
                .languageTag("en-US")
                .build();
    }

    private static Stream<Arguments> provideTokensForParameterizedTests() {
        return Stream.of(
                Arguments.of(Named.of("Owner permission level", OWNER_TOKEN)),
                Arguments.of(Named.of("Facility Manager permission level", FACILITY_MANAGER_TOKEN))
        );
    }

}

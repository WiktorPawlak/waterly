package pl.lodz.p.it.ssbd2023.ssbd06.integration.mok;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto.Operation.GRANT;
import static pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto.Operation.REVOKE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
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
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.PostgresDBTestResource;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState;

@QuarkusTest
@QuarkusTestResource(value = PostgresDBTestResource.class, restrictToAnnotatedClass = true)
@Order(4)
class AccountControllerTest extends IntegrationTestsConfig {

    @Nested
    class RegisterTest {
        @Test
        @Disabled
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
    }

    @Nested
    class CreateAccountTest {

        @Test
        @SneakyThrows
        void shouldCreateAccountWhenCorrectData() {
            // given
            CreateAccountDto accountDto = prepareCreateAccountDto();

            // when
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(accountDto)
                    .when()
                    .post(ACCOUNT_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());

            // then
            String accountStateBefore = databaseConnector.executeQuery(
                    "SELECT account_state FROM account WHERE login = 'test'"
            ).getString("account_state");

            assertEquals(accountStateBefore, AccountState.CONFIRMED.name());
        }

        @ParameterizedTest(name = "{7}")
        @CsvSource({
                ",,,,,,,All null",
                "'','','','','','','',All blank",
                "' ',' ',' ',' ',' ',' ',' ', All while space",
                "1231231 23,test@test,te st,Test,Test,123,en,All incorrect"
        })
        @SneakyThrows
        void shouldNotCreateAccountWhenInCorrectData(String phoneNumber, String email, String login, String firstName, String lastName, String password,
                                                     String langTag, String topic) {
            // given
            CreateAccountDto accountDto = CreateAccountDto.builder()
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .login(login)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(password)
                    .languageTag(langTag)
                    .build();

            // then
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(accountDto)
                    .when()
                    .post(ACCOUNT_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @ParameterizedTest(name = "Violation = {3}")
        @CsvSource({
                "123123123,kontomat@gmail.com,test1,ERROR.ACCOUNT_WITH_EMAIL_EXIST",
                "123456789,test@test.com,test2,ERROR.ACCOUNT_WITH_PHONE_NUMBER_EXIST",
                "111111111,test@test.test,admin,ERROR.ACCOUNT_WITH_LOGIN_EXIST",
        })
        @SneakyThrows
        void shouldNotCreateAccountWhenConstraintViolation(String phoneNumber, String email, String login, String error) {
            // given
            CreateAccountDto accountDto = CreateAccountDto.builder()
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .login(login)
                    .firstName("Test")
                    .lastName("Test")
                    .password("p@ssword")
                    .languageTag("en-US")
                    .build();

            // then
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(accountDto)
                    .when()
                    .post(ACCOUNT_PATH)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo(error));
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldForbidCreateAccountWhenInvokedByUnauthorizedUser(String token) {
            // given
            CreateAccountDto accountDto = prepareCreateAccountDto();

            // then
            given()
                    .header(AUTHORIZATION, token)
                    .body(accountDto)
                    .when()
                    .post(ACCOUNT_PATH)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldForbidCreateAccountWhenInvokedByFacilityManager() {
            // given
            CreateAccountDto accountDto = prepareCreateAccountDto();

            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(accountDto)
                    .when()
                    .post(ACCOUNT_PATH)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldCreateOnlyOneAccountWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            int threadNumber = 10;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            List<Integer> responseCodes = new ArrayList<>(); // New list to store response codes
            CreateAccountDto accountDto = prepareCreateAccountDto();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    Response response = given()
                            .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                            .body(accountDto)
                            .when()
                            .post(ACCOUNT_PATH)
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

            assertEquals(1, responseCodes.stream().filter(responseCode -> responseCode == CREATED.getStatusCode()).toList().size());
        }

    }

    @Nested
    class AccountDeactivation {
        @Test
        @SneakyThrows
        void shouldDeactivateActiveAccount() {

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
                    .when()
                    .put(ACCOUNT_PATH + "/" + FACILITY_MANAGER_ID + "/active")
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
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.ACCOUNT_LOCKED"));
        }

        @Test
        void shouldRespondWith404WhenAccountNotFoundDuringDeactivation() {
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(DEACTIVATE_ACCOUNT)
                    .when()
                    .put(ACCOUNT_PATH + "/" + NONE_EXISTENT_ACCOUNT_ID + "/active")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonAdminUsersToDeactivateAccount(String token) {
            given()
                    .header(AUTHORIZATION, token)
                    .body(DEACTIVATE_ACCOUNT)
                    .when()
                    .put(ACCOUNT_PATH + "/" + ADMIN_ID + "/active")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }
    }

    @Nested
    class AccountActivation {
        @Test
        @SneakyThrows
        void shouldActivateInactiveAccount() {

            //given
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(DEACTIVATE_ACCOUNT)
                    .when()
                    .put(ACCOUNT_PATH + "/" + FACILITY_MANAGER_ID + "/active")
                    .then()
                    .statusCode(OK.getStatusCode());
            boolean inactive = !databaseConnector.executeQuery(
                    "SELECT active FROM account WHERE login = 'tomdut'"
            ).getBoolean("active");
            assertTrue(inactive);
            given()
                    .body(FACILITY_MANAGER_CREDENTIALS)
                    .when()
                    .post(AUTH_PATH + "/login")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.ACCOUNT_LOCKED"));

            //when
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(ACTIVATE_ACCOUNT)
                    .when()
                    .put(ACCOUNT_PATH + "/" + FACILITY_MANAGER_ID + "/active")
                    .then()
                    .statusCode(OK.getStatusCode());

            //then
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
        }

        @Test
        void shouldRespondWith404WhenAccountNotFoundDuringActivation() {
            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(ACTIVATE_ACCOUNT)
                    .when()
                    .put(ACCOUNT_PATH + "/" + NONE_EXISTENT_ACCOUNT_ID + "/active")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonAdminUsersToActivateAccount(String token) {
            given()
                    .header(AUTHORIZATION, token)
                    .body(ACTIVATE_ACCOUNT)
                    .when()
                    .put(ACCOUNT_PATH + "/" + ADMIN_ID + "/active")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }
    }

    @Nested
    class GrantRole {

        @Test
        void grantRoleGreenPathTest() {
            //given
            AccountDto ownerAccount = getOwnerAccount();
            assertEquals(1, ownerAccount.getRoles().size());
            assertEquals("OWNER", ownerAccount.getRoles().get(0));
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(GRANT);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + ownerAccount.getId() + "/roles")
                    .then()
                    .statusCode(OK.getStatusCode());

            ownerAccount = getOwnerAccount();
            assertEquals(2, ownerAccount.getRoles().size());
            assertTrue(ownerAccount.getRoles().contains("OWNER"));
            assertTrue(ownerAccount.getRoles().contains("ADMINISTRATOR"));
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldFailIfNonAdministratorTriesToGrantRoles(String token) {
            AccountDto ownerAccount = getOwnerAccount();
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(GRANT);

            given()
                    .header(AUTHORIZATION, token)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + ownerAccount.getId() + "/roles")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldFailGrantRolesIfAccountDontExists() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(GRANT);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/12/roles")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @Test
        void shouldFailGrantRolesIfRequestBodyIsSyntaticalyIncorrect() {
            EditAccountRolesDto bodyWithWrongPermissions = new EditAccountRolesDto();
            bodyWithWrongPermissions.setOperation(GRANT);
            bodyWithWrongPermissions.setRoles(Set.of("SHOULD_FAIL"));

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(bodyWithWrongPermissions)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldFailGrantRolesIfRoleIsAlreadyAdded() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("OWNER"));
            editAccountRolesDto.setOperation(GRANT);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.CANNOT_MODIFY_PERMISSIONS"));
        }

        @Test
        void shouldFailWhenTryingToAddSamePermissionTwice() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(GRANT);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(OK.getStatusCode());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.CANNOT_MODIFY_PERMISSIONS"));
        }

        @Test
        void shouldFailWhenTryingToModifyOwnPermissions() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(GRANT);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getAdministratorAccount().getId() + "/roles")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldCreateOnlyOneRentWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            int threadNumber = 10;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            List<Integer> responseCodes = new ArrayList<>();
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(GRANT);

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    Response response = given()
                            .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                            .body(editAccountRolesDto)
                            .when()
                            .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
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
    class RevokeRole {
        @Test
        void revokeRoleGreenPathTest() {
            //given
            AccountDto ownerAccount = getOwnerAccount();
            assertEquals(1, ownerAccount.getRoles().size());
            assertEquals("OWNER", ownerAccount.getRoles().get(0));
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("OWNER"));
            editAccountRolesDto.setOperation(REVOKE);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + ownerAccount.getId() + "/roles")
                    .then()
                    .statusCode(OK.getStatusCode());

            ownerAccount = getOwnerAccount();
            assertEquals(0, ownerAccount.getRoles().size());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldFailIfNonAdministratorTriesToRevokeRole(String token) {
            AccountDto ownerAccount = getOwnerAccount();
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("OWNER"));
            editAccountRolesDto.setOperation(REVOKE);

            given()
                    .header(AUTHORIZATION, token)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + ownerAccount.getId() + "/roles")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldFailRevokeRolesIfAccountDontExists() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("OWNER"));
            editAccountRolesDto.setOperation(REVOKE);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/12/roles")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @Test
        void shouldFailGrantRolesIfRequestBodyIsSyntaticalyIncorrect() {
            EditAccountRolesDto bodyWithWrongPermissions = new EditAccountRolesDto();
            bodyWithWrongPermissions.setOperation(REVOKE);
            bodyWithWrongPermissions.setRoles(Set.of("SHOULD_FAIL"));

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(bodyWithWrongPermissions)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldFailRevokeRolesIfRoleIsNotPresent() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(REVOKE);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.CANNOT_MODIFY_PERMISSIONS"));
        }

        @Test
        void shouldFailWhenTryingToRemoveSamePermissionTwice() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("OWNER"));
            editAccountRolesDto.setOperation(REVOKE);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(OK.getStatusCode());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.CANNOT_MODIFY_PERMISSIONS"));
        }

        @Test
        void shouldFailWhenTryingToModifyOwnPermissions() {
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("ADMINISTRATOR"));
            editAccountRolesDto.setOperation(REVOKE);

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(editAccountRolesDto)
                    .when()
                    .put(ACCOUNT_PATH + "/" + getAdministratorAccount().getId() + "/roles")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldCreateOnlyOneRentWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            int threadNumber = 10;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            List<Integer> responseCodes = new ArrayList<>();
            EditAccountRolesDto editAccountRolesDto = new EditAccountRolesDto();
            editAccountRolesDto.setRoles(Set.of("OWNER"));
            editAccountRolesDto.setOperation(REVOKE);

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    Response response = given()
                            .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                            .body(editAccountRolesDto)
                            .when()
                            .put(ACCOUNT_PATH + "/" + getOwnerAccount().getId() + "/roles")
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
    class ChangeOwnAccountDetails {

        @Test
        void shouldEditSelfAccountDetails() {
            String firstName = "Kamil";
            String lastName = "Kowalski-Nowak";
            String phoneNumber = "000000000";
            String languageTag = "pl-PL";

            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

            EditAccountDetailsDto dto = new EditAccountDetailsDto(ownerAccount._1.getId(),
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
        @CsvSource({
                "''",
                "mati@mati",
                "mati.com",
        })
        void whenEditOwnEmailAndDataIsIncorrectShouldReturnBadRequest(String email) {
            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

            EditEmailDto dto = new EditEmailDto(ownerAccount._1.getId(), email, ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
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
        @CsvSource({
                ",,,,",
                "' ',' ',' ',' '",
                "'','','',''",
                "mati mati,mati 123,mati mati,mati mati",
        })
        void whenUpdateSelfAccountDetailsAndDataIsIncorrectShouldReturnBadRequest(String firstName, String lastName, String phoneNumber, String languageTag) {
            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

            EditAccountDetailsDto dto =
                    new EditAccountDetailsDto(ownerAccount._1.getId(),
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

        @Test
        void shouldEditOwnAccountAccountDetails() {
            String firstName = "Kamil";
            String lastName = "Kowalski-Nowak";
            String phoneNumber = "000000000";
            String languageTag = "pl-PL";

            Tuple2<AccountDto, String> administratorAccount = getAdministratorAccountWithEtag();

            EditAccountDetailsDto dto = new EditAccountDetailsDto(administratorAccount._1.getId(),
                    firstName,
                    lastName,
                    phoneNumber,
                    languageTag,
                    administratorAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dto)
                    .header(IF_MATCH_HEADER_NAME, administratorAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + "/self")
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            assertEquals(firstName, getAdministratorAccount().getFirstName());
            assertEquals(lastName, getAdministratorAccount().getLastName());
            assertEquals(phoneNumber, getAdministratorAccount().getPhoneNumber());
        }
    }

    @Nested
    class ChangeOtherAccountDetails {

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonAdminUsersToUpdateOtherAccountsEmail(String token) {
            String email = "mati@mati.com";

            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
            EditEmailDto dto = new EditEmailDto(ownerAccount._1.getId(), email, ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, token)
                    .body(dto)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + "/2/email")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());

        }

        @ParameterizedTest
        @CsvSource({
                "''",
                "mati@mati",
                "mati.com",
        })
        void whenEditOtherAccountEmailAndDataIsIncorrectShouldReturnBadRequest(String email) {
            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();
            EditEmailDto dto = new EditEmailDto(ownerAccount._1.getId(), email, ownerAccount._1.getVersion());

            given()
                    .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                    .body(dto)
                    .header(IF_MATCH_HEADER_NAME, ownerAccount._2)
                    .when()
                    .put(ACCOUNT_PATH + "/1")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].field", notNullValue())
                    .body("[0].message", notNullValue());
        }

        @ParameterizedTest
        @CsvSource({
                ",,,,",
                "' ',' ',' ',' '",
                "'','','',''",
                "mati mati,mati 123,mati mati,mati mati",
        })
        void whenUpdateOtherAccountDetailsAndDataIsIncorrectShouldReturnBadRequest(String firstName, String lastName, String phoneNumber, String languageTag) {
            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

            EditAccountDetailsDto dto =
                    new EditAccountDetailsDto(ownerAccount._1.getId(),
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
                    .put(ACCOUNT_PATH + "/1")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].field", notNullValue())
                    .body("[0].message", notNullValue());
        }

        @Test
        void shouldEditOtherAccountAccountDetails() {
            String firstName = "Kamil";
            String lastName = "Kowalski-Nowak";
            String phoneNumber = "000000000";
            String languageTag = "pl-PL";

            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

            EditAccountDetailsDto dto = new EditAccountDetailsDto(ownerAccount._1.getId(),
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
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mok.AccountControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonAdminUsersToUpdateOtherAccountsAccountDetails(String token) {
            String firstName = "Kamil";
            String lastName = "Kowalski-Nowak";
            String phoneNumber = "000000000";
            String languageTag = "pl-PL";

            Tuple2<AccountDto, String> ownerAccount = getOwnerAccountWithEtag();

            EditAccountDetailsDto dto =
                    new EditAccountDetailsDto(ownerAccount._1.getId(),
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
                    .statusCode(FORBIDDEN.getStatusCode());
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

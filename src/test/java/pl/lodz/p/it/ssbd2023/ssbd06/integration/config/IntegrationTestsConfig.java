package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

public class IntegrationTestsConfig extends PayaraContainerInitializer {

    protected static final String AUTH_PATH = "/auth";
    protected static final String ACCOUNT_PATH = "/accounts";
    protected static final Credentials ADMIN_CREDENTIALS = new Credentials("admin", "admin12345");
    protected static final Credentials OWNER_CREDENTIALS = new Credentials("new", "jantes123");
    protected static final Credentials FACILITY_MANAGER_CREDENTIALS = new Credentials("tomdut", "jantes123");
    protected static final AccountActiveStatusDto DEACTIVATE_ACCOUNT = AccountActiveStatusDto.of(false);
    protected static final AccountActiveStatusDto ACTIVATE_ACCOUNT = AccountActiveStatusDto.of(true);
    protected static final long ADMIN_ID = 1;
    protected static final long OWNER_ID = 2;
    protected static final long NONE_EXISTENT_ACCOUNT_ID = -9;

    protected static final long NOT_CONFIRMED_OWNER_ID = 4;
    protected static final long FACILITY_MANAGER_ID = 3;
    protected static final String IF_MATCH_HEADER_NAME = "If-Match";

    protected static String ADMINISTRATOR_TOKEN;
    protected static String OWNER_TOKEN;
    protected static String FACILITY_MANAGER_TOKEN;

    protected static String POSTGRES_PORT;

    protected DatabaseConnector databaseConnector;

    @BeforeAll
    protected void tokensSetup() {
        ADMINISTRATOR_TOKEN = getToken(ADMIN_CREDENTIALS);
        FACILITY_MANAGER_TOKEN = getToken(FACILITY_MANAGER_CREDENTIALS);
        OWNER_TOKEN = getToken(OWNER_CREDENTIALS);
    }

    @BeforeAll
    protected void postgresPortSetup() {
        POSTGRES_PORT = String.valueOf(postgres.getFirstMappedPort());
    }

    protected String getToken(final Credentials credentials) {
        return "Bearer " + given().body(credentials).post("/auth/login").asString();
    }

    protected AccountDto getAdministratorAccount() {
        return getAccount(ADMIN_ID);
    }

    protected AccountDto getOwnerAccount() {
        return getAccount(OWNER_ID);
    }

    protected AccountDto getNotConfirmedOwnerAccount() {
        return getAccount(NOT_CONFIRMED_OWNER_ID);
    }

    protected AccountDto getFacilityManagerAccount() {
        return getAccount(FACILITY_MANAGER_ID);
    }

    protected AccountDto getAccount(long id) {
        return given().header(AUTHORIZATION, ADMINISTRATOR_TOKEN).get(ACCOUNT_PATH + "/" + id).as(AccountDto.class);
    }

    protected Tuple2<AccountDto, String> getAdministratorAccountWithEtag() {
        return getUserWithEtag(ADMIN_ID);
    }

    protected Tuple2<AccountDto, String> getOwnerAccountWithEtag() {
        return getUserWithEtag(OWNER_ID);
    }

    protected Tuple2<AccountDto, String> getFacilityManagerAccountWithEtag() {
        return getUserWithEtag(FACILITY_MANAGER_ID);
    }

    protected Tuple2<AccountDto, String> getUserWithEtag(long id) {
        String eTag = given().header(AUTHORIZATION, ADMINISTRATOR_TOKEN).get(ACCOUNT_PATH + "/" + id).getHeader("ETag");
        AccountDto dto = given().header(AUTHORIZATION, ADMINISTRATOR_TOKEN).get(ACCOUNT_PATH + "/" + id).as(AccountDto.class);
        return Tuple.of(dto, eTag);
    }

    @BeforeEach
    @SneakyThrows
    void reinitializeDbAfterEachTest() {
        String url = "jdbc:postgresql://localhost:" + postgres.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF";
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        databaseConnector = new DatabaseConnector(POSTGRES_PORT);

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            InputStream inputStream = getClass().getResourceAsStream("/test-init.sql");
            assert inputStream != null;
            String sqlQuery = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sqlQuery);
            }
        }
    }
}

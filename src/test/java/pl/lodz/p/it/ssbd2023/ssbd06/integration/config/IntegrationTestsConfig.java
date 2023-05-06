package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import static io.restassured.RestAssured.given;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

public class IntegrationTestsConfig extends PayaraContainerInitializer {

    protected static final String AUTH_PATH = "/auth";
    protected static final String ACCOUNT_PATH = "/accounts";

    protected static final Credentials ADMIN_CREDENTIALS = new Credentials("admin", "admin12345");
    protected static final Credentials OWNER_CREDENTIALS = new Credentials("new", "jantes123");
    protected static final Credentials FACILITY_MANAGER_CREDENTIALS = new Credentials("tomdut", "jantes123");
    protected static final AccountActiveStatusDto DEACTIVATE_ACCOUNT = AccountActiveStatusDto.of(false);
    protected static final String ADMIN_ID = "1";
    protected static final String OWNER_ID = "2";
    protected static final String FACILITY_MANAGER_ID = "3";

    protected String ADMINISTRATOR_TOKEN;
    protected String OWNER_TOKEN;
    protected String FACILITY_MANAGER_TOKEN;

    @BeforeAll
    protected void tokensSetup() {
        ADMINISTRATOR_TOKEN = getToken(ADMIN_CREDENTIALS);
        FACILITY_MANAGER_TOKEN = getToken(FACILITY_MANAGER_CREDENTIALS);
        OWNER_TOKEN = getToken(OWNER_CREDENTIALS);
    }

    protected String getToken(final Credentials credentials) {
        return given().body(credentials).post("/auth/login").asString();
    }

    protected Stream<String> provideTokensForParameterizedTests() {
        return Stream.of(OWNER_TOKEN, FACILITY_MANAGER_TOKEN);
    }

    @AfterEach
    @SneakyThrows
    void reinitializeDbAfterEachTest() {
        String url = "jdbc:postgresql://localhost:" + postgres.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF";
        String username = postgres.getUsername();
        String password = postgres.getPassword();

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

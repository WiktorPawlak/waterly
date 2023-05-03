package pl.lodz.p.it.ssbd2023.ssbd06.config;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;

import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;

public class IntegrationTestsConfig extends PayaraContainerInitializer {

    protected String ADMINISTRATOR_TOKEN;
    protected String OWNER_TOKEN;
    protected String FACILITY_MANAGER_TOKEN;

    private String getToken(String login, String password) {
        Credentials credentials = new Credentials(login, password);

        return given().body(credentials).post("/auth/login").asString();
    }

    @BeforeAll
    protected void tokensSetup() {
        ADMINISTRATOR_TOKEN = getToken("admin", "admin12345");
        FACILITY_MANAGER_TOKEN = getToken("tomdut", "jantes123");
        OWNER_TOKEN = getToken("new", "jantes123");
    }

}

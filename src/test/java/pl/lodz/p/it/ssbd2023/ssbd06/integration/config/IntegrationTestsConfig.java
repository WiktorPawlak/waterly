package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.parsing.Parser;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.TariffsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProviderImpl;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTestsConfig {

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;
    @ConfigProperty(name = "quarkus.datasource.jdbc.first-port")
    String firstMapperPort;
    @ConfigProperty(name = "quarkus.datasource.username")
    String dbUsername;
    @ConfigProperty(name = "quarkus.datasource.password")
    String dbPassword;

    protected static final String AUTH_PATH = "/auth";
    protected static final String ACCOUNT_PATH = "/accounts";
    protected static final String WATERMETER_PATH = "/water-meters";
    protected static final String APARTMENT_PATH = "/apartments";
    protected static final String CHANGE_OWNER_PATH = "/owner";
    protected static final String INVOICE_PATH = "/invoices";
    protected static final String TARIFF_PATH = "/tariffs";

    protected static final long ADMIN_ID = 1;
    protected static final long OWNER_ID = 2;
    protected static final long NONE_EXISTENT_ACCOUNT_ID = -9;

    protected static final long MOL_OWNER_ID = 5;
    protected static final long NOT_CONFIRMED_OWNER_ID = 4;
    protected static final long FACILITY_MANAGER_ID = 3;

    protected static final long APARTMENT_ID = 1;
    protected static final long SECOND_APARTMENT_ID = 2;
    protected static final String IF_MATCH_HEADER_NAME = "If-Match";
    protected static final String TEST_INVOICE_NUMBER = "FV 2020/01/23";
    protected static final String TEST_INVOICE_DATE = "2023-10";

    protected static final long COLD_WATER_METER_ID = 1;
    protected static final long HOT_WATER_METER_ID = 2;
    protected static final long MAIN_WATER_METER_ID = 3;
    protected static final long NEW_WATER_METER_ID = 4;

    protected static final long NEW_OWNER_ID = 5;
    protected static final Credentials ADMIN_CREDENTIALS = new Credentials("admin", "admin12345");
    protected static final Credentials OWNER_CREDENTIALS = new Credentials("new", "jantes123");
    protected static final Credentials FACILITY_MANAGER_CREDENTIALS = new Credentials("tomdut", "jantes123");
    protected static final AccountActiveStatusDto DEACTIVATE_ACCOUNT = AccountActiveStatusDto.of(false);
    protected static final AccountActiveStatusDto ACTIVATE_ACCOUNT = AccountActiveStatusDto.of(true);
    protected static final WaterMeterActiveStatusDto DEACTIVATE_WATER_METER = WaterMeterActiveStatusDto.of(false);
    protected static final WaterMeterActiveStatusDto ACTIVATE_WATER_METER = WaterMeterActiveStatusDto.of(true);
    protected static final BigDecimal STARTING_VALUE = BigDecimal.valueOf(100.000);
    protected static final String SERIAL_NUMBER = "123456789 D";
    protected static final String TEST_DATE = "2024-06-10";
    protected static final String TEST_EXPECTED_MONTHLY_USAGE = "100";
    protected static final CreateMainWaterMeterDto CREATE_MAIN_WATER_METER_DTO = CreateMainWaterMeterDto.of(SERIAL_NUMBER, STARTING_VALUE, TEST_DATE);
    protected static final AssignWaterMeterDto CORRECT_ASSIGN_WATER_METER_DTO = AssignWaterMeterDto.of(SERIAL_NUMBER, STARTING_VALUE,
            TEST_DATE, "HOT_WATER", TEST_EXPECTED_MONTHLY_USAGE);
    protected static final AssignWaterMeterDto WRONG_DATE_ASSIGN_WATER_METER_DTO = AssignWaterMeterDto.of(SERIAL_NUMBER, STARTING_VALUE,
            "2020-06-10", "HOT_WATER", TEST_EXPECTED_MONTHLY_USAGE);
    protected static final AssignWaterMeterDto WRONG_TYPE_ASSIGN_WATER_METER_DTO = AssignWaterMeterDto.of(SERIAL_NUMBER, STARTING_VALUE,
            TEST_DATE, "WRONG_TYPE", TEST_EXPECTED_MONTHLY_USAGE);
    protected static final UpdateWaterMeterDto UPDATE_WATER_METER_DTO = UpdateWaterMeterDto.of(
            NEW_WATER_METER_ID,
            SERIAL_NUMBER,
            BigDecimal.valueOf(10),
            TEST_DATE,
            "12",
            SECOND_APARTMENT_ID,
            0
    );

    protected static String ADMINISTRATOR_TOKEN;
    protected static String OWNER_TOKEN;
    protected static String FACILITY_MANAGER_TOKEN;

    protected DatabaseConnector databaseConnector;

    protected IntegrationTestsConfig() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBasePath("/api")
                .setPort(8082)
                .setContentType(JSON)
                .build();
        RestAssured.defaultParser = Parser.JSON;
        if (ADMINISTRATOR_TOKEN == null) {
            ADMINISTRATOR_TOKEN = getToken(ADMIN_CREDENTIALS);
        }

        if (FACILITY_MANAGER_TOKEN == null) {
            FACILITY_MANAGER_TOKEN = getToken(FACILITY_MANAGER_CREDENTIALS);
        }

        if (OWNER_TOKEN == null) {
            OWNER_TOKEN = getToken(OWNER_CREDENTIALS);
        }

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
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

    protected Tuple2<WaterMeterDto, String> getWaterMeterWithEtag(long id) {
        String eTag = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(WATERMETER_PATH + "/" + id).getHeader("ETag");
        WaterMeterDto dto = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(WATERMETER_PATH + "/" + id).as(WaterMeterDto.class);
        return Tuple.of(dto, eTag);
    }

    protected Tuple2<ApartmentDto, String> getApartmentWithEtag(long id) {
        String eTag = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(APARTMENT_PATH + "/" + id).getHeader("ETag");
        ApartmentDto dto = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(APARTMENT_PATH + "/" + id).as(ApartmentDto.class);
        return Tuple.of(dto, eTag);
    }

    protected static TimeProvider timeProvider() {
        return new TimeProviderImpl();
    }


    protected Tuple2<InvoicesDto, String> getInvoiceWithEtag(long invoiceId) {
        String eTag = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(INVOICE_PATH + "/" + invoiceId).getHeader("ETag");
        InvoicesDto dto = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(INVOICE_PATH + "/" + invoiceId).as(InvoicesDto.class);
        return Tuple.of(dto, eTag);
    }

    protected Tuple2<TariffsDto, String> getTariffWithEtag(long tariffId) {
        String eTag = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(TARIFF_PATH + "/" + tariffId).getHeader("ETag");
        TariffsDto dto = given().header(AUTHORIZATION, FACILITY_MANAGER_TOKEN).get(TARIFF_PATH + "/" + tariffId).as(TariffsDto.class);
        return Tuple.of(dto, eTag);
    }

    @BeforeEach
    @SneakyThrows
    void reinitializeDbAfterEachTest() {
        String url = "jdbc:mariadb://localhost:" + firstMapperPort + "/ssbd06?loggerLevel=OFF";
        databaseConnector = new DatabaseConnector(firstMapperPort);

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            InputStream inputStream = getClass().getResourceAsStream("/test-init.sql");
            assert inputStream != null;
            String sqlQuery = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sqlQuery);
            }
        }
    }
}

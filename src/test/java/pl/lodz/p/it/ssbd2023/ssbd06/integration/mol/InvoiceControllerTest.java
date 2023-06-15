package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.vavr.Tuple2;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;

public class InvoiceControllerTest extends IntegrationTestsConfig {

    @Nested
    class CreateInvoice {

        @SneakyThrows
        @Test
        void shouldCreateInvoice() {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber(TEST_INVOICE_NUMBER)
                    .date(TEST_INVOICE_DATE)
                    .totalCost(BigDecimal.valueOf(100))
                    .waterUsage(BigDecimal.valueOf(200))
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());

            // then
            String invoiceNumber = databaseConnector.executeQuery(
                    "SELECT invoice_number FROM invoice WHERE id = 1"
            ).getString("invoice_number");
            String date = databaseConnector.executeQuery(
                    "SELECT date FROM invoice WHERE id = 1"
            ).getString("date");
            String totalCost = databaseConnector.executeQuery(
                    "SELECT total_cost FROM invoice WHERE id = 1"
            ).getString("total_cost");
            String waterUsage = databaseConnector.executeQuery(
                    "SELECT water_usage FROM invoice WHERE id = 1"
            ).getString("water_usage");

            assertEquals(TEST_INVOICE_NUMBER, invoiceNumber);
            assertEquals(TEST_INVOICE_DATE + "-01", date);
            assertEquals("100.00", totalCost);
            assertEquals("200.000", waterUsage);
        }

        @SneakyThrows
        @Test
        void shouldFailCreateInvoiceWhenInvoiceNumberIsInvalid() {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber("invalid number")
                    .date(TEST_INVOICE_DATE)
                    .totalCost(BigDecimal.valueOf(100))
                    .waterUsage(BigDecimal.valueOf(200))
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].message", equalTo("VALIDATION.INVOICE_INVALID_NUMBER"));
        }

        @SneakyThrows
        @Test
        void shouldFailCreateInvoiceWhenDateIsInvalid() {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber(TEST_INVOICE_NUMBER)
                    .date("123456789-05-20")
                    .totalCost(BigDecimal.valueOf(100))
                    .waterUsage(BigDecimal.valueOf(200))
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].message", equalTo("VALIDATION.INVOICE_DATE_PATTERN"));
        }

        @Test
        void shouldFailCreateInvoiceWhenThereIsNoTariff() {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber(TEST_INVOICE_NUMBER)
                    .date("2031-12")
                    .totalCost(BigDecimal.valueOf(100))
                    .waterUsage(BigDecimal.valueOf(200))
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.TARIFF_NOT_FOUND_FOR_INVOICE"));
        }

        @Test
        void shouldFailCreateInvoiceWhenThereAreNoWaterMeterMeterCheck() {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber(TEST_INVOICE_NUMBER)
                    .date("2031-05")
                    .totalCost(BigDecimal.valueOf(100))
                    .waterUsage(BigDecimal.valueOf(200))
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body("message", equalTo("ERROR.NOT_ALL_WATER_METER_CHECKS_PERFORMED"));
        }

        @ParameterizedTest(name = "date: {0}, totalCost: {1}")
        @CsvSource({
                " , 100.00",
                "2043-10-10, "
        })
        void shouldForbidInvoiceCreationWhenDataIsInvalid(String date, BigDecimal totalCost) {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber(TEST_INVOICE_NUMBER)
                    .date(date)
                    .totalCost(totalCost)
                    .waterUsage(BigDecimal.valueOf(200))
                    .build();


            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }
    }

    @Nested
    class UpdateInvoice {

        @BeforeAll
        static void configureObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            RestAssured.config = RestAssured.config().objectMapperConfig(
                    new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
        }

        @SneakyThrows
        @Test
        void shouldUpdateInvoice() {
            initInvoice();

            Tuple2<InvoicesDto, String> invoiceWithEtag = getInvoiceWithEtag(1);

            InvoicesDto updateInvoiceDto = invoiceWithEtag._1;
            updateInvoiceDto.setWaterUsage(BigDecimal.valueOf(200));
            updateInvoiceDto.setTotalCost(BigDecimal.valueOf(700));

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(updateInvoiceDto)
                    .when()
                    .header("If-Match", invoiceWithEtag._2)
                    .put(INVOICE_PATH + "/1")
                    .then()
                    .statusCode(OK.getStatusCode());

            // then
            String invoiceNumber = databaseConnector.executeQuery(
                    "SELECT invoice_number FROM invoice WHERE id = 1"
            ).getString("invoice_number");
            String date = databaseConnector.executeQuery(
                    "SELECT date FROM invoice WHERE id = 1"
            ).getString("date");
            String totalCost = databaseConnector.executeQuery(
                    "SELECT total_cost FROM invoice WHERE id = 1"
            ).getString("total_cost");
            String waterUsage = databaseConnector.executeQuery(
                    "SELECT water_usage FROM invoice WHERE id = 1"
            ).getString("water_usage");

            assertEquals("700.00", totalCost);
            assertEquals("200.000", waterUsage);
        }

        @Test
        void shouldForbidInvoiceUpdateWhenDateIsColliding() {
            initInvoice();

            Tuple2<InvoicesDto, String> invoiceWithEtag = getInvoiceWithEtag(1);
            InvoicesDto updateInvoiceDto = invoiceWithEtag._1;
            updateInvoiceDto.setDate(LocalDate.of(2099, 10, 10));

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(updateInvoiceDto)
                    .when()
                    .header("If-Match", invoiceWithEtag._2)
                    .put(INVOICE_PATH + "/1")
                    .then()
                    .statusCode(CONFLICT.getStatusCode());
        }

        @ParameterizedTest(name = "date: {0}, totalCost: {1}")
        @CsvSource({
                " , 100.00",
                "2043-10-10, "
        })
        void shouldForbidInvoiceUpdateWhenDataIsInvalid(LocalDate date, BigDecimal totalCost) {
            initInvoice();

            Tuple2<InvoicesDto, String> invoiceWithEtag = getInvoiceWithEtag(1);
            InvoicesDto updateInvoiceDto = invoiceWithEtag._1;
            updateInvoiceDto.setDate(date);
            updateInvoiceDto.setTotalCost(totalCost);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(updateInvoiceDto)
                    .when()
                    .header("If-Match", invoiceWithEtag._2)
                    .put(INVOICE_PATH + "/1")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldForbidInvoiceUpdateForNotFacilityManager() {
            initInvoice();

            Tuple2<InvoicesDto, String> invoiceWithEtag = getInvoiceWithEtag(1);
            InvoicesDto updateInvoiceDto = invoiceWithEtag._1;

            given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .body(updateInvoiceDto)
                    .when()
                    .header("If-Match", invoiceWithEtag._2)
                    .put(INVOICE_PATH + "/1")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldForbidInvoiceUpdateWithNoAuthorization() {
            initInvoice();

            Tuple2<InvoicesDto, String> invoiceWithEtag = getInvoiceWithEtag(1);
            InvoicesDto updateInvoiceDto = invoiceWithEtag._1;

            given()
                    .body(updateInvoiceDto)
                    .when()
                    .header("If-Match", invoiceWithEtag._2)
                    .put(INVOICE_PATH + "/1")
                    .then()
                    .statusCode(UNAUTHORIZED.getStatusCode());
        }

        private void initInvoice() {
            CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                    .invoiceNumber(TEST_INVOICE_NUMBER)
                    .date(TEST_INVOICE_DATE)
                    .totalCost(BigDecimal.valueOf(100.00))
                    .waterUsage(BigDecimal.valueOf(200.00))
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createInvoiceDto)
                    .when()
                    .post(INVOICE_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());
        }
    }
}

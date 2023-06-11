package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;

import java.math.BigDecimal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;

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
    }
}

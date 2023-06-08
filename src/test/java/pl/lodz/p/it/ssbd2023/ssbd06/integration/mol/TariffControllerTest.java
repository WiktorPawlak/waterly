package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;

import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateTariffDto;

@Order(7)
public class TariffControllerTest extends IntegrationTestsConfig {
    @Nested
    class TariffCreation {

        @SneakyThrows
        @Test
        void shouldCreateTariff() {
            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate(TEST_DATE)
                    .endDate(TEST_DATE)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createTariffDto)
                    .when()
                    .post("/tariffs")
                    .then()
                    .statusCode(CREATED.getStatusCode());
        }

        @Test
        void shouldForbidTariffWhenDatesAreColliding() {
            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate(TEST_DATE)
                    .endDate(TEST_DATE)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createTariffDto)
                    .when()
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createTariffDto)
                    .when()
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(CONFLICT.getStatusCode());
        }

        @Test
        void shouldForbidTariffCreationForNotFacilityManager() {
            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate(TEST_DATE)
                    .endDate(TEST_DATE)
                    .build();

            given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .body(createTariffDto)
                    .when()
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @ParameterizedTest(name = "coldWaterPrice: {0}, hotWaterPrice: {1}, trashPrice: {2}")
        @CsvSource({
                "1.00, ,1.00",
                " ,60.00,2.00",
                "2.00,12.00, ",
        })
        void shouldForbidTariffCreationWithValidPrices(BigDecimal coldWaterPrice, BigDecimal hotWaterPrice, BigDecimal trashPrice) {
            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(coldWaterPrice)
                    .hotWaterPrice(hotWaterPrice)
                    .trashPrice(trashPrice)
                    .startDate(TEST_DATE)
                    .endDate(TEST_DATE)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createTariffDto)
                    .when()
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @ParameterizedTest(name = "startDate: {0}, endDate: {1}")
        @CsvSource({
                "201-3-invalid, 2023-05-10",
                "2023-05-10, test"
        })
        void shouldForbidTariffCreationWithInvalidDates(String startDate, String endDate) {
            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createTariffDto)
                    .when()
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }
    }

    private static Stream<Arguments> provideTokensForParameterizedTests() {
        return Stream.of(
                Arguments.of(Named.of("Owner permission level", OWNER_TOKEN)),
                Arguments.of(Named.of("Administrator permission level", ADMINISTRATOR_TOKEN)),
                Arguments.of(Named.of("Facility manager permission level", FACILITY_MANAGER_TOKEN))
        );
    }
}

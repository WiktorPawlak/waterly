package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.response.Response;
import io.vavr.Tuple2;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.MariaDBTestResource;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateTariffDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.TariffsDto;

@QuarkusTest
@QuarkusTestResource(value = MariaDBTestResource.class)
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
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());

            TariffsDto dto = given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .get(TARIFF_PATH + "/4")
                    .as(TariffsDto.class);

            assertEquals(new BigDecimal("100.00"), dto.getColdWaterPrice());
            assertEquals(new BigDecimal("100.00"), dto.getHotWaterPrice());
            assertEquals(new BigDecimal("100.00"), dto.getTrashPrice());
            assertEquals(LocalDate.parse("2024-06-01"), dto.getStartDate());
            assertEquals(LocalDate.parse("2024-06-30"), dto.getEndDate());
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

        @Test
        void shouldForbidTariffCreationWithNoAuth() {
            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate(TEST_DATE)
                    .endDate(TEST_DATE)
                    .build();

            given()
                    .body(createTariffDto)
                    .when()
                    .post(TARIFF_PATH)
                    .then()
                    .statusCode(UNAUTHORIZED.getStatusCode());
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

        @SneakyThrows
        @Test
        void shouldCreateOnlyOneTariffWithConcurrentRequests() {
            int threadNumber = 50;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            List<Integer> responseCodes = new ArrayList<>();

            CreateTariffDto createTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate(TEST_DATE)
                    .endDate(TEST_DATE)
                    .build();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    Response response = given()
                            .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                            .body(createTariffDto)
                            .when()
                            .post(TARIFF_PATH)
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
    class TariffUpdate {

        @BeforeAll
        static void configureObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            RestAssured.config = RestAssured.config().objectMapperConfig(
                    new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
        }

        @Test
        @SneakyThrows
        void shouldEditTariffProperly() {
            initTariff();

            Tuple2<TariffsDto, String> tariffWithEtag = getTariffWithEtag(1);
            TariffsDto editTariffDto = tariffWithEtag._1;
            editTariffDto.setHotWaterPrice(BigDecimal.valueOf(200));
            editTariffDto.setColdWaterPrice(BigDecimal.valueOf(300));
            editTariffDto.setTrashPrice(BigDecimal.valueOf(400));

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(editTariffDto)
                    .when()
                    .header("If-Match", tariffWithEtag._2)
                    .put(TARIFF_PATH + "/1")
                    .then()
                    .statusCode(OK.getStatusCode());

            String hotWaterPrice = databaseConnector.executeQuery(
                    "SELECT hot_water_price FROM tariff WHERE id = 1"
            ).getString("hot_water_price");
            String coldWaterPrice = databaseConnector.executeQuery(
                    "SELECT cold_water_price FROM tariff WHERE id = 1"
            ).getString("cold_water_price");
            String trashPrice = databaseConnector.executeQuery(
                    "SELECT trash_price FROM tariff WHERE id = 1"
            ).getString("trash_price");

            assertEquals("200.00", hotWaterPrice);
            assertEquals("300.00", coldWaterPrice);
            assertEquals("400.00", trashPrice);
        }

        @Test
        void shouldForbidTariffEditWhenDatesAreColliding() {
            initTariff();

            CreateTariffDto createAnotherTariffDto = CreateTariffDto.builder()
                    .coldWaterPrice(STARTING_VALUE)
                    .hotWaterPrice(STARTING_VALUE)
                    .trashPrice(STARTING_VALUE)
                    .startDate("2025-06-10")
                    .endDate("2027-06-10")
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createAnotherTariffDto)
                    .when()
                    .post("/tariffs")
                    .then()
                    .statusCode(CREATED.getStatusCode());

            Tuple2<TariffsDto, String> tariffWithEtag = getTariffWithEtag(1);
            TariffsDto editTariffDto = tariffWithEtag._1;

            editTariffDto.setStartDate(LocalDate.of(2020, 5, 10));
            editTariffDto.setEndDate(LocalDate.of(2029, 7, 10));

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(editTariffDto)
                    .when()
                    .header("If-Match", tariffWithEtag._2)
                    .put(TARIFF_PATH + "/1")
                    .then()
                    .statusCode(CONFLICT.getStatusCode());
        }

        @Test
        void shouldForbidTariffEditWhenStartDateIsAfterExpiryDate() {
            initTariff();

            Tuple2<TariffsDto, String> tariffWithEtag = getTariffWithEtag(1);
            TariffsDto editTariffDto = tariffWithEtag._1;

            editTariffDto.setStartDate(LocalDate.of(2029, 5, 10));
            editTariffDto.setEndDate(LocalDate.of(2028, 7, 10));

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(editTariffDto)
                    .when()
                    .header("If-Match", tariffWithEtag._2)
                    .put(TARIFF_PATH + "/1")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldForbidTariffEditForNotFacilityManager() {
            initTariff();

            Tuple2<TariffsDto, String> tariffWithEtag = getTariffWithEtag(1);
            TariffsDto editTariffDto = tariffWithEtag._1;
            editTariffDto.setHotWaterPrice(BigDecimal.valueOf(200));
            editTariffDto.setColdWaterPrice(BigDecimal.valueOf(300));
            editTariffDto.setTrashPrice(BigDecimal.valueOf(400));

            given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .body(editTariffDto)
                    .when()
                    .header("If-Match", tariffWithEtag._2)
                    .put(TARIFF_PATH + "/1")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @ParameterizedTest(name = "startDate: {0}, endDate: {1}")
        @CsvSource({
                " , 2023-05-10",
                "2023-02-22,  "
        })
        void shouldForbidTariffEditWithInvalidData(LocalDate startDate, LocalDate endDate) {
            initTariff();

            Tuple2<TariffsDto, String> tariffWithEtag = getTariffWithEtag(1);
            TariffsDto editTariffDto = tariffWithEtag._1;

            editTariffDto.setStartDate(startDate);
            editTariffDto.setEndDate(endDate);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(editTariffDto)
                    .when()
                    .header("If-Match", tariffWithEtag._2)
                    .put(TARIFF_PATH + "/1")
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

    private void initTariff() {
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
}

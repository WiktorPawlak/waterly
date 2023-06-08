package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.vavr.Tuple2;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType;

@Order(6)
public class WaterMeterControllerTest extends IntegrationTestsConfig {

    @Nested
    class WaterMeterDeactivation {
        @Test
        @SneakyThrows
        void shouldDeactivateActiveWaterMeter() {

            //given
            boolean active = databaseConnector.executeQuery(
                    "SELECT active FROM water_meter WHERE id = 1"
            ).getBoolean("active");
            assertTrue(active);

            //when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(DEACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID + "/active")
                    .then()
                    .statusCode(OK.getStatusCode());

            //then
            boolean inactive = !databaseConnector.executeQuery(
                    "SELECT active FROM water_meter WHERE id = 1"
            ).getBoolean("active");
            assertTrue(inactive);
        }

        @Test
        void shouldRespondWith404WhenWaterMeterNotFoundDuringDeactivation() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(DEACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + NONE_EXISTENT_ACCOUNT_ID + "/active")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.WaterMeterControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonFacilityManagerUsersToDeactivateWaterMeter(String token) {
            given()
                    .header(AUTHORIZATION, token)
                    .body(DEACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID + "/active")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode())
                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
        }
    }

    @Nested
    class WaterMeterActivation {
        @Test
        @SneakyThrows
        void shouldActivateInactiveWaterMeter() {

            //given
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(DEACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID + "/active")
                    .then()
                    .statusCode(OK.getStatusCode());
            boolean inactive = !databaseConnector.executeQuery(
                    "SELECT active FROM water_meter WHERE id = 1"
            ).getBoolean("active");
            assertTrue(inactive);

            //when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(ACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID + "/active")
                    .then()
                    .statusCode(OK.getStatusCode());

            //then
            boolean active = databaseConnector.executeQuery(
                    "SELECT active FROM water_meter WHERE id = 1"
            ).getBoolean("active");
            assertTrue(active);
        }

        @Test
        void shouldRespondWith404WhenWaterMeterNotFoundDuringActivation() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(ACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + NONE_EXISTENT_ACCOUNT_ID + "/active")
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.WaterMeterControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonFacilityManagerUsersToActivateWaterMeter(String token) {
            given()
                    .header(AUTHORIZATION, token)
                    .body(ACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID + "/active")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode())
                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
        }
    }

    @Nested
    class CreateMainWaterMeter {

        @Test
        @SneakyThrows
        void shouldCreateMainWaterMeterWhenNoActiveOneAlready() {
            // given
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(DEACTIVATE_WATER_METER)
                    .when()
                    .put(WATERMETER_PATH + "/" + MAIN_WATER_METER_ID + "/active");

            // when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CREATE_MAIN_WATER_METER_DTO)
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(CREATED.getStatusCode());

            // then
            String type = databaseConnector.executeQuery(
                    "SELECT type FROM water_meter WHERE id = 3"
            ).getString("type");
            String startingValue = databaseConnector.executeQuery(
                    "SELECT starting_value FROM water_meter WHERE id = 3"
            ).getString("starting_value");

            assertEquals(WaterMeterType.MAIN.name(), type);
            assertEquals("100.000", startingValue);
        }

        @Test
        void shouldNotCreateMainWaterMeterWhenActiveOneAlready() {
            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CREATE_MAIN_WATER_METER_DTO)
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(CONFLICT.getStatusCode());
        }

        @Test
        void shouldNotCreateMainWaterMeterWhenInvalidDateFormat() {
            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CreateMainWaterMeterDto.of(STARTING_VALUE, "invalid-format"))
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldNotCreateMainWaterMeterWhenDateAlreadyExpired() {
            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CreateMainWaterMeterDto.of(STARTING_VALUE, "1970-01-01"))
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.WaterMeterControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonFacilityManagerUsersToCreateMainWaterMeter(String token) {
            given()
                    .header(AUTHORIZATION, token)
                    .body(CREATE_MAIN_WATER_METER_DTO)
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode())
                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
        }

    }

    @Nested
    class UpdateWaterMeter {

        @Test
        @SneakyThrows
        void shouldUpdateWaterMeterWhenCorrectData() {
            // given
            Tuple2<WaterMeterDto, String> waterMeterWithEtag = getWaterMeterWithEtag(WATER_METER_ID);

            // when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, waterMeterWithEtag._2)
                    .body(UPDATE_WATER_METER_DTO)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            // then
            ResultSet resultSet = databaseConnector.executeQuery(
                    "SELECT * FROM water_meter WHERE id = " + WATER_METER_ID
            );
            assertEquals(resultSet.getString("starting_value"), "10.000");
            assertEquals(resultSet.getString("expected_usage"), "12.000");
            assertEquals(resultSet.getString("apartment_id"), String.valueOf(SECOND_APARTMENT_ID));
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.WaterMeterControllerTest#provideUpdateWaterMeterDtosForParameterizedTests")
        void shouldUpdateWaterMeterWhenSomeDataNotPresent(UpdateWaterMeterDto dto) {
            // given
            Tuple2<WaterMeterDto, String> waterMeterWithEtag = getWaterMeterWithEtag(WATER_METER_ID);

            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, waterMeterWithEtag._2)
                    .body(dto)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());
        }

        @Test
        @SneakyThrows
        void shouldUpdateOnlyExpiryDateInMainWaterMeterWhenRedundantData() {
            // given
            Tuple2<WaterMeterDto, String> waterMeterWithEtag = getWaterMeterWithEtag(MAIN_WATER_METER_ID);

            String expiryDateBefore = databaseConnector.executeQuery(
                    "SELECT expiry_date FROM water_meter WHERE id = " + MAIN_WATER_METER_ID
            ).getString("expiry_date");

            // when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, waterMeterWithEtag._2)
                    .body(UpdateWaterMeterDto.builder()
                            .id(MAIN_WATER_METER_ID)
                            .startingValue(BigDecimal.ONE)
                            .expiryDate(TEST_DATE)
                            .apartmentId(APARTMENT_ID)
                            .version(waterMeterWithEtag._1.getVersion())
                            .build())
                    .when()
                    .put(WATERMETER_PATH + "/" + MAIN_WATER_METER_ID)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            // then
            ResultSet resultSetAfter = databaseConnector.executeQuery(
                    "SELECT * FROM water_meter WHERE id = " + MAIN_WATER_METER_ID
            );
            assertNotEquals(resultSetAfter.getString("starting_value"), BigDecimal.ONE.toString());
            assertNull(resultSetAfter.getString("apartment_id"));
            assertNotEquals(resultSetAfter.getString("expiry_date"), expiryDateBefore);
        }

        @Test
        void shouldNotUpdateWaterMeterWhenConflictingData() {
            // given
            Tuple2<WaterMeterDto, String> waterMeterWithEtag = getWaterMeterWithEtag(WATER_METER_ID);

            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, waterMeterWithEtag._2)
                    .body(UpdateWaterMeterDto.builder()
                            .id(WATER_METER_ID)
                            .expiryDate(TEST_DATE)
                            .apartmentId(123L)
                            .version(waterMeterWithEtag._1.getVersion())
                            .build())
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @ParameterizedTest
        @CsvSource({
                "invalid-format",
                "1970-01-01",
        })
        void shouldNotUpdateWaterMeterWhenInvalidData(String expiryDate) {
            // given
            Tuple2<WaterMeterDto, String> waterMeterWithEtag = getWaterMeterWithEtag(WATER_METER_ID);

            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, waterMeterWithEtag._2)
                    .body(UpdateWaterMeterDto.builder()
                            .id(WATER_METER_ID)
                            .expiryDate(expiryDate)
                            .version(waterMeterWithEtag._1.getVersion())
                            .build())
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.WaterMeterControllerTest#provideTokensForParameterizedTests")
        void shouldForbidNonFacilityManagerUsersToCreateMainWaterMeter(String token) {
            // given
            Tuple2<WaterMeterDto, String> waterMeterWithEtag = getWaterMeterWithEtag(WATER_METER_ID);

            // then
            given()
                    .header(AUTHORIZATION, token)
                    .header(IF_MATCH_HEADER_NAME, waterMeterWithEtag._2)
                    .body(UPDATE_WATER_METER_DTO)
                    .when()
                    .put(WATERMETER_PATH + "/" + WATER_METER_ID)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode())
                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
        }

    }

    @Nested
    class ApartmentGetWaterMeterList {

        @Test
        void shouldGetWaterMetersForApartmentId() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .get(WATERMETER_PATH + "/apartment/" + APARTMENT_ID)
                    .then()
                    .statusCode(OK.getStatusCode())
                    .extract().body().jsonPath().getList("data", WaterMeter.class);
        }
    }

    private static Stream<Arguments> provideTokensForParameterizedTests() {
        return Stream.of(
                Arguments.of(Named.of("Owner permission level", OWNER_TOKEN))
                //Arguments.of(Named.of("Administrator permission level", ADMINISTRATOR_TOKEN))
                //Admin też ma role facility manager
        );
    }

    private static Stream<Arguments> provideUpdateWaterMeterDtosForParameterizedTests() {
        return Stream.of(
                Arguments.of(Named.of("No apartmentId",
                        UpdateWaterMeterDto.builder()
                                .id(WATER_METER_ID)
                                .startingValue(BigDecimal.ONE)
                                .expiryDate(TEST_DATE)
                                .expectedUsage(BigDecimal.ONE)
                                .version(0)
                                .build())),
                Arguments.of(Named.of("No startingValue",
                        UpdateWaterMeterDto.builder()
                                .id(WATER_METER_ID)
                                .expiryDate(TEST_DATE)
                                .expectedUsage(BigDecimal.ONE)
                                .apartmentId(1L)
                                .version(0)
                                .build())),
                Arguments.of(Named.of("No expectedUsage",
                        UpdateWaterMeterDto.builder()
                                .id(WATER_METER_ID)
                                .startingValue(BigDecimal.ONE)
                                .expiryDate(TEST_DATE)
                                .apartmentId(1L)
                                .version(0)
                                .build()))
        );
    }
}

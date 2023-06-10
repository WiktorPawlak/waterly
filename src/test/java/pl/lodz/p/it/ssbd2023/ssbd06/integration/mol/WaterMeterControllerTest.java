package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;

import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
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
                    "SELECT type FROM water_meter WHERE id = 2"
            ).getString("type");
            assertEquals(WaterMeterType.MAIN.name(), type);
        }

        @Test
        @SneakyThrows
        void shouldNotCreateMainWaterMeterWhenActiveOneAlready() {
            // given
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CREATE_MAIN_WATER_METER_DTO)
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(CREATED.getStatusCode());

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
        @SneakyThrows
        void shouldNotCreateMainWaterMeterWhenInvalidDateFormat() {
            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CreateMainWaterMeterDto.of("invalid-format"))
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

        @Test
        @SneakyThrows
        void shouldNotCreateMainWaterMeterWhenDateAlreadyExpired() {
            // then
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CreateMainWaterMeterDto.of("1970-01-01"))
                    .when()
                    .post(WATERMETER_PATH + "/main-water-meter")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
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
}

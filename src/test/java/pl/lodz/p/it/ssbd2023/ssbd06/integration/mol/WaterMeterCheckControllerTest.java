package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_CHECK_WAS_ALREADY_PERFORMED;
import static pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto.Operation.GRANT;
import static pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto.Operation.REVOKE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.PostgresDBTestResource;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterChecksDto;

@QuarkusTest
@QuarkusTestResource(value = PostgresDBTestResource.class, restrictToAnnotatedClass = true)
@Order(8)
class WaterMeterCheckControllerTest extends IntegrationTestsConfig {

    public static final BigDecimal COLD_WATER_METER_READING = BigDecimal.valueOf(500.000);
    public static final BigDecimal HOT_WATER_METER_READING = BigDecimal.valueOf(300.000);
    public static final WaterMeterChecksDto FM_CHECKS_DTO = WaterMeterChecksDto.of(List.of(
            WaterMeterCheckDto.of(COLD_WATER_METER_ID, COLD_WATER_METER_READING),
            WaterMeterCheckDto.of(HOT_WATER_METER_ID, HOT_WATER_METER_READING)
    ), "2023-06-14", true);
    public static final WaterMeterChecksDto OWNER_CHECKS_DTO = WaterMeterChecksDto.of(List.of(
            WaterMeterCheckDto.of(COLD_WATER_METER_ID, COLD_WATER_METER_READING),
            WaterMeterCheckDto.of(HOT_WATER_METER_ID, HOT_WATER_METER_READING)
    ), "2023-06-14", false);

    @Test
    @SneakyThrows
    void shouldPerformFirstWaterMeterCheckFM() {
        //given
        //when
        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .body(FM_CHECKS_DTO)
                .when()
                .post(WATERMETER_PATH + "/water-meter-checks")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        //then
        BigDecimal coldWaterUsage = databaseConnector.executeQuery(
                "SELECT s.cold_water_usage FROM water_usage_stats s WHERE s.year_month = '2023-06-01'"
        ).getBigDecimal("cold_water_usage");
        assertEquals(BigDecimal.valueOf(1000000).movePointLeft(3), coldWaterUsage);
    }

    @ParameterizedTest(name = "coldWaterPrice: {0}, hotWaterPrice: {1}, trashPrice: {2}")
    @CsvSource({
            " 1,100.321321,2023-06-01",
            "1,100.321,badDate",
    })
    @SneakyThrows
    void shouldFailForBadRequestData(Long waterMeterId, BigDecimal meterReading, String checkDate) {
        //given
        WaterMeterChecksDto dto = WaterMeterChecksDto.of(List.of(
                        WaterMeterCheckDto.of(waterMeterId, meterReading),
                        WaterMeterCheckDto.of(waterMeterId, meterReading)),
                checkDate, true);
        //when && then
        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .body(dto)
                .when()
                .post(WATERMETER_PATH + "/water-meter-checks")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @SneakyThrows
    void shouldNotAllowOwnerForWaterMeterCheckWhenFacilityManagerAlreadyMadeOneInGivenMonth() {
        //given
        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .body(FM_CHECKS_DTO)
                .when()
                .post(WATERMETER_PATH + "/water-meter-checks")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        //when
        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(OWNER_CHECKS_DTO)
                .when()
                .post(WATERMETER_PATH + "/water-meter-checks")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("message", equalTo(ERROR_CHECK_WAS_ALREADY_PERFORMED));
    }

    @Test
    void shouldForbidNonFacilityManagerAndOwnerUsersToPerformWaterMeterCheck() {
        //given
        String newFacilityManagerToken = makeFacilityManagerOnlyAdministrator();

        //when
        //then
        given()
                .header(AUTHORIZATION, newFacilityManagerToken)
                .body(FM_CHECKS_DTO)
                .when()
                .post(WATERMETER_PATH + "/water-meter-checks")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    private String makeFacilityManagerOnlyAdministrator() {
        AccountDto facilityManagerAccount = getFacilityManagerAccount();
        EditAccountRolesDto grantAdminRole = new EditAccountRolesDto();
        grantAdminRole.setRoles(Set.of("ADMINISTRATOR"));
        grantAdminRole.setOperation(GRANT);
        EditAccountRolesDto revokeManagerRole = new EditAccountRolesDto();
        revokeManagerRole.setRoles(Set.of("FACILITY_MANAGER"));
        revokeManagerRole.setOperation(REVOKE);

        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(grantAdminRole)
                .when()
                .put(ACCOUNT_PATH + "/" + facilityManagerAccount.getId() + "/roles")
                .then()
                .statusCode(OK.getStatusCode());
        given()
                .header(AUTHORIZATION, ADMINISTRATOR_TOKEN)
                .body(revokeManagerRole)
                .when()
                .put(ACCOUNT_PATH + "/" + facilityManagerAccount.getId() + "/roles")
                .then()
                .statusCode(OK.getStatusCode());
        return "Bearer " + given()
                .body(FACILITY_MANAGER_CREDENTIALS)
                .when()
                .post(AUTH_PATH + "/login")
                .asString();
    }

    @Test
    void shouldRespondWith404WhenWaterMeterNotFound() {
        var invalidWaterMeterIdDto = OWNER_CHECKS_DTO;
        invalidWaterMeterIdDto.getWaterMeterChecks().get(0).setWaterMeterId(NONE_EXISTENT_ACCOUNT_ID);
        given()
                .header(AUTHORIZATION, OWNER_TOKEN)
                .body(invalidWaterMeterIdDto)
                .when()
                .post(WATERMETER_PATH + "/water-meter-checks")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }
}

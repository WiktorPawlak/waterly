package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.math.BigDecimal;
import java.util.List;
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
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterExpectedUsagesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType;

@Order(6)
class ApartmentControllerTest extends IntegrationTestsConfig {
    public static final BigDecimal EXPECTED_USAGE = BigDecimal.valueOf(300.000);
    public static final ChangeApartmentOwnerDto CHANGE_APARTMENT_OWNER_DTO = ChangeApartmentOwnerDto.builder()
            .newOwnerId(OWNER_ID)
            .waterMeterExpectedUsages(List.of(
                    WaterMeterExpectedUsagesDto.of(COLD_WATER_METER_ID, EXPECTED_USAGE),
                    WaterMeterExpectedUsagesDto.of(HOT_WATER_METER_ID, EXPECTED_USAGE)
            ))
            .version(0)
            .build();

    @Nested
    class ApartmentCreation {

        @Test
        void shouldCreateApartment() {
            String number = "60a";
            BigDecimal area = BigDecimal.valueOf(2.44);

            CreateApartmentDto createAccountDto = CreateApartmentDto.builder()
                    .area(area)
                    .number(number)
                    .ownerId(getOwnerAccount().getId())
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createAccountDto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());

            ApartmentDto apartmentDto = given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .get(APARTMENT_PATH + "/3")
                    .then()
                    .statusCode(OK.getStatusCode())
                    .extract().as(ApartmentDto.class);

            assertEquals(number, apartmentDto.getNumber());
            assertEquals(area, apartmentDto.getArea());
        }

        @ParameterizedTest(name = "area: {0}, ownerId: {1}, number: {2}")
        @CsvSource({
                "0.9,1,60a",
                "1000,1,60a",
                "1,,60a",
                ",1,60a",
                "2,1,12   a",
        })
        void shouldReturnBadRequestWhenBodyHaveWrongForm(BigDecimal area, Long ownerId, String number) {
            CreateApartmentDto dto = CreateApartmentDto.builder()
                    .area(area)
                    .number(number)
                    .ownerId(ownerId)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].field", notNullValue())
                    .body("[0].message", notNullValue());

        }

        @Test
        void shouldReturnNotFoundWhenOwnerAccountNotExist() {
            CreateApartmentDto dto = CreateApartmentDto.builder()
                    .area(BigDecimal.ONE)
                    .number("60a")
                    .ownerId(0L)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode())
                    .body(notNullValue());
        }

        @Test
        void shouldReturnConflictWhenApartmentWithNameExist() {
            CreateApartmentDto dto = CreateApartmentDto.builder()
                    .area(BigDecimal.ONE)
                    .number("60a")
                    .ownerId(getOwnerAccount().getId())
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .post(APARTMENT_PATH);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body(notNullValue());
        }

        @Test
        void shouldReturnNotFoundWhenAccountNotHaveOwnerPermissions() {
            CreateApartmentDto dto = CreateApartmentDto.builder()
                    .area(BigDecimal.ONE)
                    .number("60a")
                    .ownerId(getFacilityManagerAccount().getId())
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode())
                    .body(notNullValue());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.ApartmentControllerTest#provideTokensForParameterizedTests")
        void shouldReturnForbiddenWhenAccountNotHaveFacilityMangerPermission(String token) {
            CreateApartmentDto createAccountDto = CreateApartmentDto.builder()
                    .area(BigDecimal.valueOf(2.4))
                    .number("60a")
                    .ownerId(getOwnerAccount().getId())
                    .build();

            given()
                    .header(AUTHORIZATION, token)
                    .body(createAccountDto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode())
                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
        }

    }

    @Nested
    class ApartmentOwnerChange {

        @Test
        void shouldChangeApartmentOwner() {

            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = CHANGE_APARTMENT_OWNER_DTO;
            changeOwnerDto.setNewOwnerId(NEW_OWNER_ID);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(OK.getStatusCode());


            ApartmentDto apartmentDto = given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .get(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(OK.getStatusCode())
                    .extract().as(ApartmentDto.class);

            assertEquals(NEW_OWNER_ID, apartmentDto.getOwnerId());
        }


        @Test
        void shouldReturnNotFoundWhenThereIsNoSuchApartmentOwner() {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = CHANGE_APARTMENT_OWNER_DTO;
            changeOwnerDto.setNewOwnerId(99L);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @Test
        void shouldReturnNotFoundWhenThereIsNoSuchApartment() {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = CHANGE_APARTMENT_OWNER_DTO;
            changeOwnerDto.setNewOwnerId(NEW_OWNER_ID);


            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/99" + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @Test
        void shouldReturnUnauthorizedWhenNoAuthToken() {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = CHANGE_APARTMENT_OWNER_DTO;
            changeOwnerDto.setNewOwnerId(NEW_OWNER_ID);

            given()
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(UNAUTHORIZED.getStatusCode());
        }

        @Test
        void shouldReturnForbiddenWhenInsufficientRoles() {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = CHANGE_APARTMENT_OWNER_DTO;
            changeOwnerDto.setNewOwnerId(NEW_OWNER_ID);

            given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode());
        }

        @ParameterizedTest(name = "waterMeterId: {0}, expectedMonthlyUsage: {1}")
        @CsvSource({
                "5, 1200",
                "4, 1200"
        })
        void shouldForbidApartmentChangeWithInvalidWaterMetersId(long waterMetedId, BigDecimal expectedMonthlyUsage) {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = ChangeApartmentOwnerDto.of(
                    OWNER_ID,
                    List.of(
                            WaterMeterExpectedUsagesDto.of(waterMetedId, expectedMonthlyUsage)
                    ), 0);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

        @ParameterizedTest(name = "waterMeterId: {0}, expectedMonthlyUsage: {1}")
        @CsvSource({
                "1, 1.111111",
                "2, 2.222222"
        })
        void shouldForbidApartmentChangeWithInvalidUsage(long waterMetedId, BigDecimal expectedMonthlyUsage) {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            ChangeApartmentOwnerDto changeOwnerDto = ChangeApartmentOwnerDto.of(
                    OWNER_ID,
                    List.of(
                            WaterMeterExpectedUsagesDto.of(waterMetedId, expectedMonthlyUsage)
                    ), 0);

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._1)
                    .body(changeOwnerDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID + CHANGE_OWNER_PATH)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode());
        }

    }

    @Nested
    class ApartmentUpdate {

        @Test
        void shouldUpdateApartment() {
            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            String number = "80b";
            BigDecimal area = BigDecimal.valueOf(24.44);

            EditApartmentDetailsDto editApartmentDto = EditApartmentDetailsDto.builder()
                    .id(apartmentWithEtag._1.getId())
                    .area(area)
                    .number(number)
                    .version(apartmentWithEtag._1.getVersion())
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._2)
                    .body(editApartmentDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(NO_CONTENT.getStatusCode());

            ApartmentDto apartmentDto = given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .get(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(OK.getStatusCode())
                    .extract().as(ApartmentDto.class);

            assertEquals(number, apartmentDto.getNumber());
            assertEquals(area, apartmentDto.getArea());
        }

        @ParameterizedTest(name = "area: {0}, number: {1}")
        @CsvSource({
                "0.9,60a",
                "1000,60a",
                "1,",
                ",60a",
                "2,12   a",
        })
        void shouldReturnBadRequestWhenBodyHaveWrongForm(BigDecimal area, String number) {
            EditApartmentDetailsDto dto = EditApartmentDetailsDto.builder()
                    .area(area)
                    .number(number)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].field", notNullValue())
                    .body("[0].message", notNullValue());

        }

        @Test
        void shouldReturnConflictWhenApartmentWithNameExist() {
            createApartment();

            Tuple2<ApartmentDto, String> apartmentWithEtag = getApartmentWithEtag(APARTMENT_ID);

            EditApartmentDetailsDto editApartmentDto = EditApartmentDetailsDto.builder()
                    .id(apartmentWithEtag._1.getId())
                    .area(BigDecimal.valueOf(24.44))
                    .number("60a")
                    .version(apartmentWithEtag._1.getVersion())
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .header(IF_MATCH_HEADER_NAME, apartmentWithEtag._2)
                    .body(editApartmentDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(CONFLICT.getStatusCode())
                    .body(notNullValue());
        }

        @ParameterizedTest
        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.ApartmentControllerTest#provideTokensForParameterizedTests")
        void shouldReturnForbiddenWhenAccountNotHaveFacilityMangerPermission(String token) {
            EditApartmentDetailsDto editApartmentDto = EditApartmentDetailsDto.builder()
                    .area(BigDecimal.valueOf(24.44))
                    .number("60b")
                    .build();

            given()
                    .header(AUTHORIZATION, token)
                    .body(editApartmentDto)
                    .when()
                    .put(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(FORBIDDEN.getStatusCode())
                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
        }

    }

    @Nested
    class ApartmentDetails {

        @Test
        void shouldReturnApartmentDetails() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .get(APARTMENT_PATH + "/" + APARTMENT_ID)
                    .then()
                    .statusCode(OK.getStatusCode())
                    .body("id", equalTo(1))
                    .body("number", equalTo("12a"))
                    .body("area", equalTo(40F))
                    .body("ownerName", equalTo("Szymon Ziemecki"));

        }

        @Test
        void shouldReturn404WhenNoApartment() {
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .when()
                    .get(APARTMENT_PATH + "/" + 123)
                    .then()
                    .statusCode(NOT_FOUND.getStatusCode());
        }

    }

    @Nested
    class ApartmentGetPaginatedLists {

        @Test
        void shouldGetPaginatedList() {
            createApartment();

            List<ApartmentDto> apartments = given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .param("page", 1)
                    .param("pageSize", 5)
                    .param("order", "asc")
                    .when()
                    .get(APARTMENT_PATH)
                    .then()
                    .statusCode(OK.getStatusCode())
                    .body("itemsInPage", equalTo(3))
                    .body("totalPages", equalTo(1))
                    .extract().body().jsonPath().getList("data", ApartmentDto.class);

            assertEquals(3, apartments.size());
        }

        @Test
        void shouldGetSelfOwnerApartments() {
            CreateApartmentDto dto = CreateApartmentDto.builder()
                    .area(BigDecimal.ONE)
                    .number("60a")
                    .ownerId(MOL_OWNER_ID)
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(dto)
                    .when()
                    .post(APARTMENT_PATH);

            List<ApartmentDto> apartments = given()
                    .header(AUTHORIZATION, OWNER_TOKEN)
                    .param("page", 1)
                    .param("pageSize", 5)
                    .param("order", "asc")
                    .when()
                    .get(APARTMENT_PATH + "/self")
                    .then()
                    .statusCode(OK.getStatusCode())
                    .body("itemsInPage", equalTo(2))
                    .body("totalPages", equalTo(1))
                    .extract().body().jsonPath().getList("data", ApartmentDto.class);

            assertEquals(2, apartments.size());
        }

    }

    @Nested
    class AssignWaterMeterToApartment {

        @Test
        @SneakyThrows
        void shouldAssignWaterMeterToApartment() {
            // when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(CORRECT_ASSIGN_WATER_METER_DTO)
                    .when()
                    .post(APARTMENT_PATH + "/" + APARTMENT_ID + "/water-meter")
                    .then()
                    .statusCode(CREATED.getStatusCode());

            // then
            String type = databaseConnector.executeQuery(
                    "SELECT type FROM water_meter WHERE id = 4"
            ).getString("type");
            String startingValue = databaseConnector.executeQuery(
                    "SELECT starting_value FROM water_meter WHERE id = 4"
            ).getString("starting_value");
            String expiryDate = databaseConnector.executeQuery(
                    "SELECT expiry_date FROM water_meter WHERE id = 4"
            ).getString("expiry_date");

            assertEquals(WaterMeterType.HOT_WATER.name(), type);
            assertEquals("100.000", startingValue);
            assertEquals(TEST_DATE + " 00:00:00", expiryDate);
        }

        @Test
        @SneakyThrows
        void shouldFailWhenExpiryDateIsNotInFuture() {
            // when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(WRONG_DATE_ASSIGN_WATER_METER_DTO)
                    .when()
                    .post(APARTMENT_PATH + "/" + APARTMENT_ID + "/water-meter")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("message", equalTo("ERROR.EXPIRY_DATE_ALREADY_EXPIRED"));
        }

        @Test
        @SneakyThrows
        void shouldFailWhenTypeIsInvalid() {
            // when
            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(WRONG_TYPE_ASSIGN_WATER_METER_DTO)
                    .when()
                    .post(APARTMENT_PATH + "/" + APARTMENT_ID + "/water-meter")
                    .then()
                    .statusCode(BAD_REQUEST.getStatusCode())
                    .body("[0].message", equalTo("VALIDATION.WATER_METERS_INVALID_TYPE"));
        }
    }

    private void createApartment() {
        CreateApartmentDto dto = CreateApartmentDto.builder()
                .area(BigDecimal.ONE)
                .number("60a")
                .ownerId(getOwnerAccount().getId())
                .build();

        given()
                .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                .body(dto)
                .when()
                .post(APARTMENT_PATH);
    }

    private static Stream<Arguments> provideTokensForParameterizedTests() {
        return Stream.of(
                Arguments.of(Named.of("Owner permission level", OWNER_TOKEN))
                //Arguments.of(Named.of("Administrator permission level", ADMINISTRATOR_TOKEN))
        );
    }

}
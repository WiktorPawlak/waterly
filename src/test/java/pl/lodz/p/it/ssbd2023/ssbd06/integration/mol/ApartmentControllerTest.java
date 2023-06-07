package pl.lodz.p.it.ssbd2023.ssbd06.integration.mol;

import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;

import pl.lodz.p.it.ssbd2023.ssbd06.integration.config.IntegrationTestsConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;

class ApartmentControllerTest extends IntegrationTestsConfig {

    @Nested
    class ApartmentCreation {

        @Test
        void shouldCreateApartment() {
            CreateApartmentDto createAccountDto = CreateApartmentDto.builder()
                    .area(BigDecimal.valueOf(2.4))
                    .number("60a")
                    .ownerId(getOwnerAccount().getId())
                    .build();

            given()
                    .header(AUTHORIZATION, FACILITY_MANAGER_TOKEN)
                    .body(createAccountDto)
                    .when()
                    .post(APARTMENT_PATH)
                    .then()
                    .statusCode(CREATED.getStatusCode());

        }

        @ParameterizedTest(name = "area: {0}, ownerId: {1}")
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
        void shouldReturnNotFoundWhenApartmentWithNameExist() {
            CreateApartmentDto dto = CreateApartmentDto.builder()
                    .area(BigDecimal.ONE)
                    .number("60a")
                    .ownerId(0L)
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
                    .statusCode(NOT_FOUND.getStatusCode())
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
//In next PR
//        @ParameterizedTest
//        @MethodSource("pl.lodz.p.it.ssbd2023.ssbd06.integration.mol.ApartmentControllerTest#provideTokensForParameterizedTests")
//        void shouldReturnForbiddenWhenAccountNotHaveFacilityMangerPermission(String token) {
//            CreateApartmentDto createAccountDto = CreateApartmentDto.builder()
//                    .area(BigDecimal.valueOf(2.4))
//                    .number("60a")
//                    .ownerId(getOwnerAccount().getId())
//                    .build();
//
//            given()
//                    .header(AUTHORIZATION, token)
//                    .body(createAccountDto)
//                    .when()
//                    .post(APARTMENT_PATH)
//                    .then()
//                    .statusCode(FORBIDDEN.getStatusCode())
//                    .body("message", equalTo("ERROR.FORBIDDEN_OPERATION"));
//        }

    }

    private static Stream<Arguments> provideTokensForParameterizedTests() {
        return Stream.of(
                Arguments.of(Named.of("Owner permission level", OWNER_TOKEN)),
                Arguments.of(Named.of("Administrator permission level", ADMINISTRATOR_TOKEN))
        );
    }

}
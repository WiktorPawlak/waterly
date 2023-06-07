package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentArea;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentNumber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApartmentDto {

    @ApartmentNumber
    String number;
    @ApartmentArea
    BigDecimal area;
    @NotNull
    @Nonnegative
    Long ownerId;

    //TODO Water meters list

    public Apartment toDomain(final Account ownerAccount) {
        return Apartment.builder().area(area).number(number).owner(ownerAccount).build();
    }
}

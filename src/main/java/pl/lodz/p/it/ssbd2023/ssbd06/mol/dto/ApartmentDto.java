package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {

    private long id;
    private String number;
    private BigDecimal area;
    private String ownerName;

    public ApartmentDto(final Apartment apartment) {
        this.id = apartment.getId();
        this.number = apartment.getNumber();
        this.area = apartment.getArea();

        AccountDetails accountDetails = apartment.getOwner().getAccountDetails();
        this.ownerName = accountDetails.getFirstName() + ' ' + accountDetails.getLastName();
    }

}

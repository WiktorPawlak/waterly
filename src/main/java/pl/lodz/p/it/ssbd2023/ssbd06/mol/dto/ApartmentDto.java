package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto implements Signable {

    private long id;
    private String number;
    private BigDecimal area;
    private long ownerId;
    private String ownerName;
    private long version;

    public ApartmentDto(final Apartment apartment) {
        this.id = apartment.getId();
        this.number = apartment.getNumber();
        this.area = apartment.getArea();
        this.ownerId = apartment.getOwnerId();
        this.version = apartment.getVersion();

        AccountDetails accountDetails = apartment.getOwner().getAccountDetails();
        this.ownerName = accountDetails.getFirstName() + ' ' + accountDetails.getLastName();
    }

    @Override
    public String createPayload() {
        return id + version + Apartment.class.getSimpleName();
    }

}

package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UpdateWaterMeterDto implements Signable {

    private long id;

    private BigDecimal startingValue;

    @ExpiryDate
    private String expiryDate;

    private BigDecimal expectedUsage;

    private Long apartmentId;

    private long version;

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }
}

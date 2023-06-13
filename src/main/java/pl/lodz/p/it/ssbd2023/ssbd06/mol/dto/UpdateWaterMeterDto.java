package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsage;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsageNullable;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UpdateWaterMeterDto implements Signable {

    @Nonnegative
    private long id;
    @WaterUsage
    private BigDecimal startingValue;
    @ExpiryDate
    private String expiryDate;
    @WaterUsageNullable
    private String expectedDailyUsage;
    @Nonnegative
    private Long apartmentId;
    @Nonnegative
    private long version;

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }
}

package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.SerialNumber;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterMeterDto implements Signable {

    @Nonnegative
    private long id;
    @SerialNumber
    private String serialNumber;
    private boolean active;
    @ExpiryDate
    private String expiryDate;
    @WaterUsage
    private BigDecimal expectedDailyUsage;
    @WaterUsage
    private BigDecimal startingValue;
    @NotEmpty
    private String type;
    @Nonnegative
    private Long apartmentId;
    @Nonnegative
    private long version;

    public WaterMeterDto(final WaterMeter waterMeter) {
        this.id = waterMeter.getId();
        this.serialNumber = waterMeter.getSerialNumber();
        this.active = waterMeter.isActive();
        this.expiryDate = DateConverter.convert(waterMeter.getExpiryDate());
        this.expectedDailyUsage = waterMeter.getExpectedDailyUsage();
        this.startingValue = waterMeter.getStartingValue();
        this.type = waterMeter.getType().toString();
        this.apartmentId = waterMeter.getApartment() != null ? waterMeter.getApartment().getId() : null;
        this.version = waterMeter.getVersion();
    }

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }
}

package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterMeterDto implements Signable {

    private long id;
    private boolean active;

    @ExpiryDate
    private String expiryDate;

    private BigDecimal expectedUsage;

    private BigDecimal startingValue;

    @NotNull
    private String type;

    private Long apartmentId;

    private long version;

    public WaterMeterDto(final WaterMeter waterMeter) {
        this.id = waterMeter.getId();
        this.active = waterMeter.isActive();
        this.expiryDate = DateConverter.convert(waterMeter.getExpiryDate());
        this.expectedUsage = waterMeter.getExpectedUsage();
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

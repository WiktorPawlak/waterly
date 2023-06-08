package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterMetersDto {

    private long id;
    private boolean active;

    @NotNull
    private Date expiryDate;
    @NotNull
    private BigDecimal expectedUsage;
    @NotNull
    private BigDecimal startingValue;
    @NotNull
    private String type;
    @NotNull
    private Long apartmentId;

    public WaterMetersDto(final WaterMeter waterMeter) {
        this.id = waterMeter.getId();
        this.active = waterMeter.isActive();
        this.expiryDate = waterMeter.getExpiryDate();
        this.expectedUsage = waterMeter.getExpectedUsage();
        this.startingValue = waterMeter.getStartingValue();
        this.type = waterMeter.getType().toString();
        this.apartmentId = waterMeter.getApartment().getId();
    }
}

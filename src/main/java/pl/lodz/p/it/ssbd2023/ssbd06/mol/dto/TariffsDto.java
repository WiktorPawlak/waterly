package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffsDto implements Signable {

    private long id;
    @NotNull
    private BigDecimal coldWaterPrice;
    @NotNull
    private BigDecimal hotWaterPrice;
    @NotNull
    private BigDecimal trashPrice;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private long version;

    public TariffsDto(final Tariff tariff) {
        this.id = tariff.getId();
        this.coldWaterPrice = tariff.getColdWaterPrice();
        this.hotWaterPrice = tariff.getHotWaterPrice();
        this.trashPrice = tariff.getTrashPrice();
        this.startDate = tariff.getStartDate();
        this.endDate = tariff.getEndDate();
        this.version = tariff.getVersion();
    }

    @Override
    public String createPayload() {
        return String.valueOf(id + version + Tariff.class.getSimpleName());
    }
}

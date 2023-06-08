package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnegative;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffsDto implements Signable {

    @Nonnegative
    private long id;
    @Money
    private BigDecimal coldWaterPrice;
    @Money
    private BigDecimal hotWaterPrice;
    @Money
    private BigDecimal trashPrice;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @Nonnegative
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
        return id + version + Tariff.class.getSimpleName();
    }
}

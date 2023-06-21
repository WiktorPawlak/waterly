package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_meter",
        indexes = {
                @Index(name = "water_meter_apartment_idx", columnList = "apartment_id")
        }
)
@NamedQuery(name = "WaterMeter.findAllActiveByType", query = "select w from WaterMeter w where w.type = :type and w.active = true")
@NamedQuery(name = "WaterMeter.findApartmentsByWaterMeters", query = "SELECT DISTINCT wm.apartment FROM WaterMeter wm WHERE wm.id IN :meterIds")
@NamedQuery(name = "WaterMeter.findAllActiveByApartmentIdAndDate", query =
        "SELECT wm FROM WaterMeter wm " +
                "WHERE wm.apartment.id = :apartmentId " +
                "AND wm.active = true " +
                "AND wm.expiryDate > :currentDate")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
public class WaterMeter extends AbstractEntity {
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "serial_number", nullable = false)
    private String serialNumber;
    @NotNull
    @Column(name = "starting_value", nullable = false, precision = 8, scale = 3)
    private BigDecimal startingValue;
    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;
    @Column(name = "expected_daily_usage", precision = 8, scale = 3)
    private BigDecimal expectedDailyUsage;
    @Column(nullable = false)
    private boolean active;
    @ToString.Exclude
    @OneToMany(mappedBy = "waterMeter", fetch = LAZY)
    private List<WaterMeterCheck> waterMeterChecks = new ArrayList<>();
    @NotNull
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private WaterMeterType type;
    @ToString.Exclude
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "apartment_id", foreignKey = @ForeignKey(name = "water_meter_apartment_fk"))
    private Apartment apartment;

    public WaterMeter(@NotNull final AssignWaterMeterDto assignWaterMeterDto, final Apartment apartment, final BigDecimal expectedDailyUsage) {
        this.serialNumber = assignWaterMeterDto.getSerialNumber();
        this.startingValue = assignWaterMeterDto.getStartingValue();
        this.expiryDate = DateConverter.convert(assignWaterMeterDto.getExpiryDate());
        this.active = true;
        this.type = WaterMeterType.valueOf(assignWaterMeterDto.getType());
        this.expectedDailyUsage = expectedDailyUsage;
        this.apartment = apartment;
    }

    public long getApartmentOwnerId() {
        return apartment.getOwnerId();
    }
}

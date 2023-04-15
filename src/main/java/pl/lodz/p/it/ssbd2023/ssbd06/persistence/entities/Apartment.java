package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "apartment",
        indexes = {
                @Index(name = "apartment_owner_idx", columnList = "owner_id")
        }
)
@Getter
@NoArgsConstructor
public class Apartment extends AbstractEntity {
    @NotNull
    @Column
    private BigDecimal area;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @OneToMany(mappedBy = "apartment", fetch = FetchType.LAZY)
    private List<WaterUsageStats> waterUsageStats = new ArrayList<>();

    @OneToMany(mappedBy = "apartment", fetch = FetchType.LAZY)
    private List<WaterMeter> waterMeters = new ArrayList<>();
}

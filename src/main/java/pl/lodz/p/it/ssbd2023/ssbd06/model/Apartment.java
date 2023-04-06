package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.IndexColumn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

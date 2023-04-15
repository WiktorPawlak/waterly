package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "bill",
        indexes = {
                @Index(name = "bill_apartment_idx", columnList = "apartment_id"),
                @Index(name = "bill_owner_idx", columnList = "owner_id"),
                @Index(name = "bill_advance_usage_idx", columnList = "advance_usage"),
                @Index(name = "bill_real_usage_idx", columnList = "real_usage")
        }
)
@Getter
@NoArgsConstructor
public class Bill extends AbstractEntity {
    @Column(nullable = false)
    private LocalDate date;
    @Column()
    private BigDecimal balance;
    @NotNull
    @ManyToOne(cascade = REFRESH, fetch = LAZY)
    @JoinColumn(name = "apartment_id", nullable = false, foreignKey = @ForeignKey(name = "bill_apartment_fk"))
    private Apartment apartment;
    @NotNull
    @ManyToOne(cascade = REFRESH, fetch = LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "bill_owner_Fk"))
    private Owner owner;
    @NotNull
    @OneToOne(cascade = {PERSIST, MERGE, REFRESH})
    @JoinColumn(name = "advance_usage", nullable = false, foreignKey = @ForeignKey(name = "bill_advance_usage"))
    private UsageReport advanceUsage;
    @OneToOne(cascade = {PERSIST, MERGE, REFRESH})
    @JoinColumn(name = "real_usage", foreignKey = @ForeignKey(name = "bill_real_usage_fk"))
    private UsageReport realUsage;
}

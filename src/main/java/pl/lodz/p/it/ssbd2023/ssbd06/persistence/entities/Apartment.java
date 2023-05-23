package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(name = "apartment", indexes = {
        @Index(name = "apartment_owner_idx", columnList = "owner_id")
})
@NamedQuery(name = "Apartment.findByOwner_Id", query = "select a from Apartment a where a.owner.id = :id")
@Getter
@NoArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
public class Apartment extends AbstractEntity {
    @NotNull
    @Column
    private BigDecimal area;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ToString.Exclude
    @NotNull
    @OneToMany(mappedBy = "apartment", fetch = FetchType.LAZY)
    private List<WaterUsageStats> waterUsageStats = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "apartment", fetch = FetchType.LAZY)
    private List<WaterMeter> waterMeters = new ArrayList<>();
}

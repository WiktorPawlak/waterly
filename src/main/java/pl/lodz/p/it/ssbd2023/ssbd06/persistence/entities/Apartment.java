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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(name = "apartment", indexes = {
        @Index(name = "apartment_owner_idx", columnList = "owner_id")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"number"}, name = "uk_apartment_name")
})
@NamedQuery(name = "Apartment.findByOwner_Id", query = "select a from Apartment a where a.owner.id = :id")
@Getter
@Builder
@NoArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
@AllArgsConstructor
public class Apartment extends AbstractEntity {

    @NotNull
    @Setter
    @Size(min = 1, max = 20)
    private String number;

    @NotNull
    @Setter
    @Column(precision = 6, scale = 2)
    private BigDecimal area;

    @ToString.Exclude
    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "apartment", fetch = FetchType.LAZY)
    private List<WaterUsageStats> waterUsageStats = new ArrayList<>();

    @Builder.Default
    @Setter
    @ToString.Exclude
    @OneToMany(mappedBy = "apartment", fetch = FetchType.LAZY)
    private List<WaterMeter> waterMeters = new ArrayList<>();

    public long getOwnerId() {
        return owner.getId();
    }
}

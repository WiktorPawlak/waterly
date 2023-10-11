package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(
        name = "bill",
        indexes = {
                @Index(name = "bill_apartment_idx", columnList = "apartment_id"),
                @Index(name = "bill_owner_idx", columnList = "owner_id"),
                @Index(name = "bill_advance_usage_idx", columnList = "advance_usage"),
                @Index(name = "bill_real_usage_idx", columnList = "real_usage")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "bill_date_apartment_owner_unique", columnNames = {"date", "apartment_id", "owner_id"})
        }
)
@Getter
@Setter
@NamedQuery(name = "Bill.findBillsWithNullRealUsage", query = "SELECT b FROM Bill b WHERE b.realUsage IS NULL")
@NamedQuery(name = "Bill.findBillsByApartmentId", query = "select b from Bill b where b.apartment.id = :apartmentId")
@NamedQuery(name = "Bill.findBillsByOwnerId", query = "select b from Bill b where b.account.id = :ownerId")
@NamedQuery(name = "Bill.findBillOwnerIdByApartmentIdAndDate",
        query = "select b.account.id from Bill b where b.apartment.id = :apartmentId and b.date = :billDate")
@NamedQuery(name = "Bill.findBillsByYearAndMonthAndApartmentId",
        query = "select b from Bill b where b.apartment.id = :apartmentId AND YEAR(b.date) = :year AND MONTH(b.date) = :month")
@NamedQuery(name = "Bill.findBillsByDate",
        query = "select b from Bill b where YEAR(b.date) = :year AND MONTH(b.date) = :month")
@NamedQuery(name = "Bill.findBillsByOwnerLogin",
        query = "select b from Bill b JOIN FETCH b.apartment a where b.account.login = :login")
@NoArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
public class Bill extends AbstractEntity {
    @Column(nullable = false)
    private LocalDate date;
    @Column
    private BigDecimal balance;
    @ToString.Exclude
    @NotNull
    @ManyToOne(cascade = REFRESH, fetch = LAZY)
    @JoinColumn(name = "apartment_id", updatable = false, nullable = false, foreignKey = @ForeignKey(name = "bill_apartment_fk"))
    private Apartment apartment;
    @ToString.Exclude
    @NotNull
    @ManyToOne(cascade = REFRESH, fetch = LAZY)
    @JoinColumn(name = "owner_id", updatable = false, nullable = false, foreignKey = @ForeignKey(name = "bill_owner_Fk"))
    private Account account;
    @ToString.Exclude
    @NotNull
    @ManyToOne(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY)
    @JoinColumn(name = "advance_usage", nullable = false, foreignKey = @ForeignKey(name = "bill_advance_usage"))
    private UsageReport advanceUsage;
    @ToString.Exclude
    @ManyToOne(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY)
    @JoinColumn(name = "real_usage", foreignKey = @ForeignKey(name = "bill_real_usage_fk"))
    private UsageReport realUsage;
}

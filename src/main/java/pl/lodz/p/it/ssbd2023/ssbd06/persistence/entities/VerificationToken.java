package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.REFRESH;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(
        name = "verification_token",
        indexes = {
                @Index(name = "account_idx", columnList = "account_id")
        }
)
@NamedQuery(name = "VerificationToken.findValidByToken",
        query = "select v from VerificationToken v where v.expiryDate > CURRENT_TIMESTAMP and v.token = :token")
@NamedQuery(name = "VerificationToken.findByAccountId", query = "select v from VerificationToken v where v.account.id = :accountId")
@NamedQuery(name = "VerificationToken.findAll", query = "select v from VerificationToken v")
@NamedQuery(name = "VerificationToken.deleteByAccountId", query = "delete from VerificationToken v where v.account.id = :accountId")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class VerificationToken extends AbstractEntity {

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ToString.Exclude
    @NotNull
    @OneToOne(cascade = {REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

    @Column(name = "expiry_date")
    private Date expiryDate;

}

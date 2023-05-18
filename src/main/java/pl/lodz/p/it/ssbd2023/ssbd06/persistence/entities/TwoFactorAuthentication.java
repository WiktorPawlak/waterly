package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.REFRESH;

import java.time.LocalDateTime;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.HashedPassword;

@ToString(callSuper = true)
@Entity
@Setter
@Getter
@NamedQuery(name = "TwoFactorAuthentication.findByAccountId",
        query = "SELECT two_factor_authenthication FROM TwoFactorAuthentication two_factor_authenthication" +
                " WHERE two_factor_authenthication.account.id = :id")
@Table(name = "two_factor_authentication", indexes = {
        @Index(name = "two_factor_authenthication_idx", columnList = "account_id")}
)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class TwoFactorAuthentication extends AbstractEntity {

    @ToString.Exclude
    @NotNull
    @Setter
    @Column(nullable = false, length = 60)
    @HashedPassword
    private String token;

    @Column(name = "token_creation_date")
    private LocalDateTime tokenCreationDate;

    @ToString.Exclude
    @NotNull
    @OneToOne(cascade = {REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false, unique = true)
    private Account account;
}

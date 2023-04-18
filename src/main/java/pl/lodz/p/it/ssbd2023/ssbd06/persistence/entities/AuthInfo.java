package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.REFRESH;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@NamedQuery(name = "AuthInfo.findByAccountId",
        query = "SELECT auth_info FROM AuthInfo auth_info WHERE auth_info.account.id = :id")
@Entity
@Table(name = "auth_info", indexes = {
        @Index(name = "auth_info_idx", columnList = "account_id")}
)
@NoArgsConstructor
@Getter
@Setter
public class AuthInfo extends AbstractEntity {

    @Column(name = "last_success_auth")
    private LocalDateTime lastSuccessAuth;

    @Column(name = "last_incorrect_auth")
    private LocalDateTime lastIncorrectAuth;

    @Column(name = "last_ip_address", length = 45)
    private String lastIpAddress;

    @Column(name = "incorrect_auth_count", nullable = false)
    private int incorrectAuthCount;

    @ToString.Exclude
    @NotNull
    @OneToOne(cascade = {REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false, unique = true)
    private Account account;

    public AuthInfo(final Account account) {
        this.account = account;
        this.incorrectAuthCount = 0;
    }
}

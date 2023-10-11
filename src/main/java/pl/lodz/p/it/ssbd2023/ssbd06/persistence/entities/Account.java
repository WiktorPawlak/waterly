package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.HashedPassword;

@ToString(callSuper = true)
@Entity
@Getter
@Table(name = "account", indexes = {
        @Index(name = "account_details_idx", columnList = "account_details_id"),
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"login"}, name = "uk_account_login"),
})
@NamedQuery(name = "Account.findByLogin", query = "select a from Account a where a.login = :login")
@NamedQuery(name = "Account.findByEmailAndWaitingEmail",
        query = "select a from Account a where a.accountDetails.email = :email or a.waitingEmail = :email")
@NamedQuery(name = "Account.findAccountByEmail",
        query = "select a from Account a where a.accountDetails.email = :email")
@NamedQuery(name = "Account.findByPhoneNumber", query = "select a from Account a where a.accountDetails.phoneNumber = :phoneNumber")
@NamedQuery(name = "Account.findByAccountState", query = "select a from Account a where a.accountState = :accountState")
@NamedQuery(name = "Account.findOwners",
        query = "select a from Account a inner join a.roles roles where roles.permissionLevel = 'OWNER' and roles.active = true ")

@NoArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class Account extends AbstractEntity {

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, updatable = false)
    private String login;
    @ToString.Exclude
    @NotNull
    @Setter
    @Column(nullable = false, length = 60)
    @HashedPassword
    private String password;
    @ToString.Exclude
    @NotNull
    @OneToMany(fetch = LAZY, cascade = {PERSIST, MERGE, REMOVE}, mappedBy = "account")
    @Setter
    private Set<Role> roles = new HashSet<>();
    @NotNull
    @Setter
    private boolean active;
    @NotNull
    @Setter
    private Locale locale;
    @Setter
    @NotNull
    @Column(name = "account_state")
    @Enumerated(EnumType.STRING)
    private AccountState accountState;
    @ToString.Exclude
    @Setter
    @NotNull
    @ManyToOne(optional = false, fetch = LAZY, cascade = {PERSIST, MERGE, REMOVE})
    @JoinColumn(name = "account_details_id")
    private AccountDetails accountDetails;
    @ToString.Exclude
    @Setter
    @NotNull
    @OneToOne(cascade = {PERSIST, MERGE, REMOVE}, mappedBy = "account", orphanRemoval = true)
    private AuthInfo authInfo;

    @Setter
    @Size(min = 5, max = 320)
    @Column(unique = true)
    private String waitingEmail;

    @NotNull
    @Column(name = "two_factor_enabled", nullable = false)
    @Setter
    private boolean twoFAEnabled;

    @NotNull
    @Column(name = "otp_secret", nullable = false)
    @ToString.Exclude
    @Setter
    private String otpSecret;

    public Account(final String login, final String password, final AccountDetails accountDetails,
                   final AuthInfo authInfo) {
        this.login = login;
        this.password = password;
        this.active = false;
        this.accountDetails = accountDetails;
        this.authInfo = authInfo;
        this.accountState = AccountState.NOT_CONFIRMED;
    }

    public boolean inRole(final String role) {
        return roles.stream().anyMatch(it -> Objects.equals(it.getPermissionLevel(), role) && it.isActive());
    }

    public long calculateVersion() {
        return this.getVersion() + this.getAccountDetails().getVersion();
    }
}

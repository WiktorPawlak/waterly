package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.HashedPassword;

@ToString(callSuper = true)
@Entity
@Getter
@Table(name = "account", indexes = {
        @Index(name = "account_details_idx", columnList = "account_details_id"),
        @Index(name = "account_waiting_details_idx", columnList = "waiting_account_details_id")
})
@NamedQuery(name = "Account.findByLogin", query = "select a from Account a where a.login = :login")
@NamedQuery(name = "Account.findByWaitingAccountDetailsUpdates_Id", query = "select a from Account a where a.waitingAccountDetails.id = :id")
@NamedQuery(name = "Account.findByAccountDetails_Email",
        query = "select a from Account a where a.accountDetails.email = :email or a.waitingAccountDetails.email = :email")
@NoArgsConstructor
public class Account extends AbstractEntity {

    @NotNull
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false, updatable = false)
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
    @OneToOne(optional = false, fetch = LAZY, cascade = {PERSIST, MERGE, REMOVE})
    @JoinColumn(name = "account_details_id")
    private AccountDetails accountDetails;
    @ToString.Exclude
    @Setter
    @NotNull
    @OneToOne(cascade = {PERSIST, MERGE, REMOVE}, mappedBy = "account", orphanRemoval = true)
    private AuthInfo authInfo;

    @ToString.Exclude
    @JoinColumn(name = "waiting_account_details_id")
    @Setter
    @OneToOne(fetch = LAZY, cascade = {PERSIST, MERGE, REMOVE})
    private AccountDetails waitingAccountDetails;

    //TODO we can add updates history?

    public Account(final String login, final String password, final AccountDetails accountDetails,
                   final AuthInfo authInfo) {
        this.login = login;
        this.password = password;
        this.active = false;
        this.accountDetails = accountDetails;
        this.authInfo = authInfo;
        this.accountState = AccountState.NOT_CONFIRMED;
    }
}

package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.validators.HashedPassword;

@Entity
@Getter
@Table(name = "account", indexes = {
        @Index(name = "account_details_idx", columnList = "account_details_id")
})
@NamedQueries({
        @NamedQuery(name = "Account.findByLogin", query = "select a from Account a where a.login = :login"),
        @NamedQuery(name = "Account.findByWaitingAccountDetailsUpdates_Id", query = "select a from Account a where a.waitingAccountDetails.id = :id")
})
@NoArgsConstructor
public class Account extends AbstractEntity {

    @NotNull
    @Size(min = 3, max = 50)
    @Column(unique = true, updatable = false)
    private String login;
    @NotNull
    @Column(nullable = false, length = 60)
    @HashedPassword
    private String password;
    @NotNull
    @OneToMany(fetch = LAZY, cascade = {PERSIST, MERGE, REMOVE}, mappedBy = "account")
    @Setter
    private Set<Role> roles = new HashSet<>();
    @NotNull
    @Setter
    private boolean active;
    @NotNull
    @Column(name = "account_state")
    @Enumerated(EnumType.STRING)
    private AccountState accountState;
    @NotNull
    @JoinColumn(name = "account_details_id")
    @Setter
    @OneToOne(optional = false, fetch = LAZY, cascade = {PERSIST, MERGE})
    private AccountDetails accountDetails;
    @NotNull
    @OneToOne(cascade = {PERSIST, MERGE}, mappedBy = "account", orphanRemoval = true)
    private AuthInfo authInfo;

    @JoinColumn(name = "waiting_account_details")
    @Setter
    @OneToOne(fetch = LAZY, cascade = {PERSIST, MERGE})
    private AccountDetails waitingAccountDetails;

    //TODO we can add updates history?
}

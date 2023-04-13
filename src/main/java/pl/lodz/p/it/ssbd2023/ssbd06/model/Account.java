package pl.lodz.p.it.ssbd2023.ssbd06.model;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Account extends AbstractEntity {

    @NotNull
    @Size(min = 3, max = 50)
    private String login;
    @NotNull
    @Size(min = 8, max = 100)
    private String password;
    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "account")
    private Collection<Role> roles = new ArrayList<>();
    @NotNull
    @Setter
    private boolean active;
    @NotNull
    @JoinColumn(name = "account_details_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private AccountDetails accountDetails;

}

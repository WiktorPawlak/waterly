package pl.lodz.p.it.ssbd2023.ssbd06.model;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "permissionlevel")
@Table(name = "role", indexes = {
        @Index(name = "role_account_idx", columnList = "account_id")
})
@Getter
@NoArgsConstructor
public class Role extends AbstractEntity {

    @NotNull
    @Size(min = 5, max = 16)
    @Column(updatable = false, insertable = false)
    private String permissionLevel;

    @NotNull
    @Setter
    private boolean active = true;

    @NotNull
    @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false, fetch = LAZY, cascade = PERSIST)
    @Setter
    private Account account;

}

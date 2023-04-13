package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "permissionlevel")
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
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @Setter
    private Account account;

}

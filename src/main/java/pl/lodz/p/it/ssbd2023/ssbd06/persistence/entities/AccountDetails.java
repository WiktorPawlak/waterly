package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_details")
@Getter
@Setter
@NoArgsConstructor
public class AccountDetails extends AbstractEntity {

    @NotNull
    @Size(min = 5, max = 50)
    @Column(unique = true)
    private String email;
    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "first_name")
    private String firstName;
    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "last_name")
    private String lastName;
    @NotNull
    @Size(min = 8, max = 9)
    @Column(name = "phone_number")
    private String phoneNumber;

}

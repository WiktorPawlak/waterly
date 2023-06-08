package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(name = "account_details", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}, name = "uk_account_email"),
        @UniqueConstraint(columnNames = {"phone_number"}, name = "uk_account_phone_number")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class AccountDetails extends AbstractEntity {

    @NotNull
    @Size(min = 5, max = 320)
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

    public AccountDetails(final String email, final String firstName, final String lastName, final String phoneNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}

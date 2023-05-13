package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.FirstName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LastName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountDto {
    private long id;

    @Login
    private String login;

    @FirstName
    private String firstName;

    @LastName
    private String lastName;

    private boolean active;

    private List<String> roles = new ArrayList<>();

    public ListAccountDto(final Account account) {
        this.id = account.getId();
        this.login = account.getLogin();
        this.firstName = account.getAccountDetails().getFirstName();
        this.lastName = account.getAccountDetails().getLastName();
        this.active = account.isActive();
        account.getRoles().stream().filter(Role::isActive).forEach(role -> roles.add(role.getPermissionLevel()));
    }
}

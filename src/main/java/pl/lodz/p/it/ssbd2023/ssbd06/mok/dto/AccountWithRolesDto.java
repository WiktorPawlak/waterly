package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.FirstName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LanguageTag;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LastName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PhoneNumber;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithRolesDto implements Signable {
    private long id;
    @Login
    private String login;

    @Email
    private String email;

    @FirstName
    private String firstName;

    @LastName
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

    @LanguageTag
    private String languageTag;

    private boolean active;

    private long version;

    private List<String> roles = new ArrayList<>();

    public AccountWithRolesDto(final Account account) {
        this.id = account.getId();
        this.login = account.getLogin();
        this.email = account.getAccountDetails().getEmail();
        this.firstName = account.getAccountDetails().getFirstName();
        this.lastName = account.getAccountDetails().getLastName();
        this.phoneNumber = account.getAccountDetails().getPhoneNumber();
        this.languageTag = account.getLocale().toLanguageTag();
        this.active = account.isActive();
        this.version = account.getVersion() + account.getAccountDetails().getVersion();
        account.getRoles().stream().filter(Role::isActive).forEach(role -> roles.add(role.getPermissionLevel()));
    }

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }

}



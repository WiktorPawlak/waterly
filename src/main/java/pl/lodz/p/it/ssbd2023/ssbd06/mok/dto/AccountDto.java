package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
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
public class AccountDto implements Signable {
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
    private List<String> roles = new ArrayList<>();
    @LanguageTag
    private String languageTag;
    private long version;
    private boolean active;
    @NotNull
    private String createdOn;
    @NotNull
    private String createdBy;
    @NotNull
    private String updatedOn;
    @NotNull
    private String updatedBy;
    @NotNull
    private String lastSuccessAuth;
    @NotNull
    private String lastIncorrectAuth;
    @NotNull
    private String lastIpAddress;
    private int incorrectAuthCount;
    private String accountState;
    private boolean twoFAEnabled;

    public AccountDto(final Account account) {
        this.id = account.getId();
        this.login = account.getLogin();
        this.email = account.getAccountDetails().getEmail();
        this.firstName = account.getAccountDetails().getFirstName();
        this.lastName = account.getAccountDetails().getLastName();
        this.phoneNumber = account.getAccountDetails().getPhoneNumber();
        this.languageTag = account.getLocale().toLanguageTag();
        this.active = account.isActive();
        this.version = account.getVersion() + account.getAccountDetails().getVersion();
        this.accountState = account.getAccountState().name();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.createdOn = account.getCreatedOn().format(formatter);
        this.createdBy = account.getCreatedBy() != null
                ? account.getCreatedBy().getLogin()
                : "System";
        this.updatedOn = account.getUpdatedOn() != null
                ? account.getUpdatedOn().format(formatter)
                : "---";
        this.updatedBy = account.getUpdatedBy() != null
                ? account.getUpdatedBy().getLogin()
                : "---";

        var authInfo = account.getAuthInfo();
        this.lastSuccessAuth = authInfo.getLastSuccessAuth() != null
                ? authInfo.getLastSuccessAuth().format(formatter)
                : "---";
        this.lastIncorrectAuth = authInfo.getLastIncorrectAuth() != null
                ? authInfo.getLastIncorrectAuth().format(formatter)
                : "---";
        this.lastIpAddress = authInfo.getLastIpAddress() != null
                ? authInfo.getLastIpAddress()
                : "---";
        this.incorrectAuthCount = authInfo.getIncorrectAuthCount();
        account.getRoles().stream().filter(Role::isActive).forEach(role -> roles.add(role.getPermissionLevel()));
        this.twoFAEnabled = account.isTwoFAEnabled();
    }

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }
}

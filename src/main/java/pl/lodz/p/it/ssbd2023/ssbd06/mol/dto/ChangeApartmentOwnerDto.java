package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;

public class ChangeApartmentOwnerDto {

    @Getter
    @Setter
    @Login
    private String newOwnerLogin;

}

package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;


import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAccountRolesDto {

    @NotNull
    private Operation operation;

    @NotNull
    private Set<String> roles;

    public enum Operation {
        GRANT,
        REVOKE
    }
}

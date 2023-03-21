package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue
    private long id;
    private String permissionLevel;

}

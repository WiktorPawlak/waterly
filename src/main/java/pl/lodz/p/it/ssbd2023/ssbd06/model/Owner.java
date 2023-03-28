package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("OWNER")
@NoArgsConstructor
public class Owner extends Role {
}

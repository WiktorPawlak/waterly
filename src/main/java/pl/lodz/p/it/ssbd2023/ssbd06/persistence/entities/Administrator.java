package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Entity
@DiscriminatorValue("ADMINISTRATOR")
@NoArgsConstructor
public class Administrator extends Role {
}

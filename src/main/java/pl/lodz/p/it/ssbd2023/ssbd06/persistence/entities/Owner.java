package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@DiscriminatorValue("OWNER")
@NoArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class Owner extends Role {
}

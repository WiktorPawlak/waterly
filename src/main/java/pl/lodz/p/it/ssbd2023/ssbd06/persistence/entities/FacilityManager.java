package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(name = "facility_manager")
@DiscriminatorValue("FACILITY_MANAGER")
@NoArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class FacilityManager extends Role {
}

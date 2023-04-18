package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Entity
@Table(name = "facility_manager")
@DiscriminatorValue("FACILITY_MANAGER")
@NoArgsConstructor
public class FacilityManager extends Role {
}

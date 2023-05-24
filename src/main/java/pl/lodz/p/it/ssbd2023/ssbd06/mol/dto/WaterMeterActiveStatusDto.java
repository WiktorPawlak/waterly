package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
public class WaterMeterActiveStatusDto {

    private boolean active;
}

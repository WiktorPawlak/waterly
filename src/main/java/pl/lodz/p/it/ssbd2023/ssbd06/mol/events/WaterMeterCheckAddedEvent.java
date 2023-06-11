package pl.lodz.p.it.ssbd2023.ssbd06.mol.events;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterChecksDto;

@Data
@AllArgsConstructor
public class WaterMeterCheckAddedEvent {
    LocalDate checkDate;
    WaterMeterChecksDto waterMeterChecksDto;
}

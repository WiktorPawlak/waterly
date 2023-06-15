package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterMeterCheckDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class WaterMeterChecksDto {

    @NotNull
    @Valid
    private List<WaterMeterCheckDto> waterMeterChecks;

    @NotNull
    @WaterMeterCheckDate
    private String checkDate;

    private boolean managerAuthored;
}

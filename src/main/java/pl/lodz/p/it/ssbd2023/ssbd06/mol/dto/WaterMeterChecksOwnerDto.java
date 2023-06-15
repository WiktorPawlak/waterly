package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class WaterMeterChecksOwnerDto {

    @NotNull
    @Valid
    private List<WaterMeterCheckDto> waterMeterChecks;
}
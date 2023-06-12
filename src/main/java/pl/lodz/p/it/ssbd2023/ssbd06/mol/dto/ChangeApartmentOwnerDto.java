package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.util.List;

import javax.annotation.Nonnegative;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ChangeApartmentOwnerDto {

    @Nonnegative
    private Long newOwnerId;
    @NotNull
    @Valid
    private List<WaterMeterExpectedUsagesDto> waterMeterExpectedUsages;
}

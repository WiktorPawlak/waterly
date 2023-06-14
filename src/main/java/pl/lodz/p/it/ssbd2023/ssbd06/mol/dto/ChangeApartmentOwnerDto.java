package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.util.List;

import javax.annotation.Nonnegative;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ChangeApartmentOwnerDto implements Signable {

    @Nonnegative
    private Long newOwnerId;
    @NotNull
    private List<@Valid WaterMeterExpectedUsagesDto> waterMeterExpectedUsages;
    @Nonnegative
    private long version;
    @Override
    public String createPayload() {
        return String.valueOf(newOwnerId + version);
    }
}

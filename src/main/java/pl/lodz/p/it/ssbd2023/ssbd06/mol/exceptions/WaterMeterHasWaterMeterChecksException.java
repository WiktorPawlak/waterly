package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class WaterMeterHasWaterMeterChecksException extends ApplicationBaseException {

    public WaterMeterHasWaterMeterChecksException() {
        super(CONFLICT, ERROR_WATER_METER_HAS_WATER_METER_CHECKS);
    }

}

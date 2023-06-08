package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class WaterMeterExpiredException extends ApplicationBaseException {

    public WaterMeterExpiredException() {
        super(BAD_REQUEST, ERROR_WATER_METER_EXPIRED);
    }
}

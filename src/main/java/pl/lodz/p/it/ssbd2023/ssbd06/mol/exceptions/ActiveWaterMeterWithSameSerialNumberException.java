package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class ActiveWaterMeterWithSameSerialNumberException extends ApplicationBaseException {

    public ActiveWaterMeterWithSameSerialNumberException() {
        super(CONFLICT, ERROR_ACTIVE_WATER_METER_WITH_SAME_SERIAL_NUMBER);
    }

}

package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class MainWaterMeterAlreadyExistsException extends ApplicationBaseException {

    public MainWaterMeterAlreadyExistsException() {
        super(CONFLICT, ERROR_MAIN_WATER_METER_ALREADY_EXISTS);
    }

}

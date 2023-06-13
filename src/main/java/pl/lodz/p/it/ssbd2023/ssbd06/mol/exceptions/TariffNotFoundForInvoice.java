package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class TariffNotFoundForInvoice extends ApplicationBaseException {
    public TariffNotFoundForInvoice() {
        super(CONFLICT, ERROR_TARIFF_NOT_FOUND_FOR_INVOICE);
    }
}

package pl.lodz.p.it.ssbd2023.ssbd06.mol.events;


import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceUpdatedEvent {
    LocalDate invoiceDate;
}

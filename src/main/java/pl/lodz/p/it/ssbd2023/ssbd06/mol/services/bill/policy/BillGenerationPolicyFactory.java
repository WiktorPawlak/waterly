package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import java.time.LocalDate;

import io.vavr.Tuple2;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;

public interface BillGenerationPolicyFactory {
    Tuple2<BillGenerationPolicy, Bill> resolveBillGenerationPolicy(LocalDate date, Apartment apartment);
}

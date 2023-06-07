package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.InvoicesOrderBy;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Page;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PageSize;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPagedInvoicesListDto {

    @Page
    private Integer page;
    @PageSize
    private Integer pageSize;
    @Order
    private String order;
    @InvoicesOrderBy
    private String orderBy;
}

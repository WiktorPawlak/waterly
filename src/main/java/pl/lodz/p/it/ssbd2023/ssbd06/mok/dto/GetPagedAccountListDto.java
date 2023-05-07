package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.AccountOrderBy;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Page;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PageSize;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPagedAccountListDto {
    @Page
    private Integer page;
    @PageSize
    private Integer pageSize;
    @Order
    private String order;
    @AccountOrderBy
    private String orderBy;
}

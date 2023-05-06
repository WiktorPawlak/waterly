package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.AccountOrderBy;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPagedAccountListDto {
    private Integer page;
    private Integer pageSize;
    @Order
    private String order;
    @AccountOrderBy
    private String orderBy;
}

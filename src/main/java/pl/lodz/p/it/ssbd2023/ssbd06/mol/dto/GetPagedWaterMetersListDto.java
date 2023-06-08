package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Page;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PageSize;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterMetersOrderBy;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPagedWaterMetersListDto {

    @Page
    private Integer page;
    @PageSize
    private Integer pageSize;
    @Order
    private String order;
    @WaterMetersOrderBy
    private String orderBy;
}

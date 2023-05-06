package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedList<T> {
    List<T> data;
    int pageNumber;
    int itemsInPage;
    Long totalPages;
}

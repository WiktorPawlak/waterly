package pl.lodz.p.it.ssbd2023.ssbd06.mol.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@ApplicationScoped
public class PaginationConfig {

    public static final int FIRST_PAGE = 1;
    @ConfigProperty(name = "default.list.page.size")
    private int defaultListPageSize;

    public int preparePage(final Integer page) {
        return page != null ? page : FIRST_PAGE;
    }

    public int preparePageSize(final Integer pageSize) {
        return pageSize != null ? pageSize : defaultListPageSize;
    }

    public String preparePattern(final String pattern) {
        return pattern != null && !pattern.isBlank() ? pattern.strip() : null;
    }

    public boolean prepareAscOrder(final String order) {
        return "asc".equalsIgnoreCase(order);
    }

}

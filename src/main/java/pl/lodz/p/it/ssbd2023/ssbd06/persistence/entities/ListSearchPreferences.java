package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.REFRESH;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MokAuditingEntityListener;

@Entity
@Table(
        name = "list_search_preferences",
        indexes = {
                @Index(name = "list_search_preferences_account_idx", columnList = "account_id")
        })
@NamedQuery(name = "ListSearchPreferences.findByAccount", query = "select lp from ListSearchPreferences lp where lp.account = :account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({MokAuditingEntityListener.class})
public class ListSearchPreferences extends AbstractEntity {

    @OneToOne(cascade = {REFRESH})
    @JoinColumn(name = "account_id", updatable = false, foreignKey = @ForeignKey(name = "list_search_preferences_account_fk"))
    private Account account;
    @Column(name = "page_size")
    @Setter
    private int pageSize;
    @Column(name = "order_by")
    @Setter
    private String orderBy;
    @Column(name = "sorting_order")
    @Setter
    private String sortingOrder;

}

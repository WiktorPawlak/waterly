package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@Entity
@Table(name = "entity_consistence_assurance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"unique_chain"}, name = "uk_entity_consistence_assurance_unique_chain")
        })
@NamedQuery(
        name = "EntityConsistenceAssurance.findByTopic",
        query = "select eca from EntityConsistenceAssurance eca where eca.topic = :topic"
)
@NamedQuery(
        name = "EntityConsistenceAssurance.findByTopicAndUniqueChain",
        query = "select eca from EntityConsistenceAssurance eca where eca.topic = :topic and eca.uniqueChain = :uniqueChain"
)
@EntityListeners({MolAuditingEntityListener.class})
@AllArgsConstructor
@NoArgsConstructor
public class EntityConsistenceAssurance extends AbstractEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "topic", updatable = false, nullable = false)
    private ConsistencyAssuranceTopic topic;

    @Column(name = "unique_chain", updatable = false)
    private String uniqueChain;

}

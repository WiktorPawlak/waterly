package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@MappedSuperclass
abstract class AbstractEntity {

    @Id
    @GeneratedValue
    private long id;
    @Version
    private long version;

}

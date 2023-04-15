package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@MappedSuperclass
abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private long version;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @OneToOne
    @JoinColumn(name = "created_by", updatable = false)
    private Account createdBy;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @OneToOne
    @JoinColumn(name = "updated_by")
    private Account updatedBy;
}

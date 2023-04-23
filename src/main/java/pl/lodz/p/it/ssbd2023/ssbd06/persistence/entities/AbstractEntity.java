package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "created_by", updatable = false)
    @Setter
    private Account createdBy;

    @UpdateTimestamp
    @Column(name = "updated_on")
    @Setter
    private LocalDateTime updatedOn;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "updated_by")
    private Account updatedBy;
}

package pl.lodz.p.it.ssbd2023.ssbd06.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private long id;
    private String login;
    private String password;
    @OneToMany
    private List<Role> roles;
    private boolean active;

}

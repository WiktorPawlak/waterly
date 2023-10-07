package pl.lodz.p.it.ssbd2023.ssbd06.service.security;


import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;

@RequestScoped
public class AuthFacade {

    @PersistenceContext(unitName = "authPU")
    private EntityManager em;

    public Optional<Account> findByLogin(final String login) {
        try {
            return Optional.of(em.createNamedQuery("Account.findByLogin", Account.class)
                    .setParameter("login", login)
                    .getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        } catch (final PersistenceException e) {
            throw ApplicationBaseException.persistenceException(e);
        }
    }

}

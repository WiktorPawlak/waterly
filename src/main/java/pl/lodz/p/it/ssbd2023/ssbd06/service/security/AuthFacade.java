package pl.lodz.p.it.ssbd2023.ssbd06.service.security;


import java.util.Optional;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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

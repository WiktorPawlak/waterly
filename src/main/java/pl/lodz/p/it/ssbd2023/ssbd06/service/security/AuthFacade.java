package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade.CAUGHT_EXCEPTION;

import java.util.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions.AccountNotFoundException;

@Stateless
public class AuthFacade {

    private final Logger log = Logger.getLogger(getClass().getName());

    @PersistenceContext(unitName = "authPU")
    private EntityManager em;

    public Account findByLogin(final String login) throws AccountNotFoundException {
        try {
            return em.createNamedQuery("Account.findByLogin", Account.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION + e);
            throw new AccountNotFoundException("Account not found");
        }
    }

}

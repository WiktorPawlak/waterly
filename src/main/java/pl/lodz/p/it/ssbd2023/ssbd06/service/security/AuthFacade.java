package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade.CAUGHT_EXCEPTION;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;

@Slf4j
@Stateless
public class AuthFacade {

    @PersistenceContext(unitName = "authPU")
    private EntityManager em;

    public Account findByLogin(final String login) {
        try {
            return em.createNamedQuery("Account.findByLogin", Account.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION, e);
            throw new RuntimeException();
        }
    }

}

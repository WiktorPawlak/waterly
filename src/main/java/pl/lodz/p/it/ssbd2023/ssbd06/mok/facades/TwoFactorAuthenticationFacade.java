package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TwoFactorAuthentication;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;

@Log
@Monitored
@Stateless
@FacadeExceptionHandler
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TwoFactorAuthenticationFacade extends AbstractFacade<TwoFactorAuthentication> {
    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public TwoFactorAuthenticationFacade() {
        super(TwoFactorAuthentication.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @OnlyGuest
    public TwoFactorAuthentication findByAccount(final Account account) {
        return em.createNamedQuery("TwoFactorAuthentication.findByAccount", TwoFactorAuthentication.class)
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("account", account)
                .setFirstResult(0)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Override
    @OnlyGuest
    public TwoFactorAuthentication create(final TwoFactorAuthentication entity) {
        return super.create(entity);
    }

    @Override
    @OnlyGuest
    public TwoFactorAuthentication update(final TwoFactorAuthentication entity) {
        return super.update(entity);
    }

    @Override
    @OnlyGuest
    public void delete(final TwoFactorAuthentication entity) {
        super.delete(entity);
    }

    @Override
    @OnlyGuest
    public TwoFactorAuthentication findById(final Long id) {
        return super.findById(id);
    }

    @Override
    @OnlyGuest
    public List<TwoFactorAuthentication> findAll() {
        return super.findAll();
    }
}

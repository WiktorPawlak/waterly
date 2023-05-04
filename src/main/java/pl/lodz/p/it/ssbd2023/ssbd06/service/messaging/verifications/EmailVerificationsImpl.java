package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.RESET_PASSWORD_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.RESET_PASSWORD_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.VERIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.VERIFICATION_MAIL_TOPIC;

import java.util.Locale;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.config.EmailConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender.EmailSenderProvider;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmailVerificationsImpl implements VerificationsProvider {

    @Inject
    private EmailSenderProvider emailSenderProvider;
    @Inject
    private I18nProvider i18n;
    @Inject
    private EmailConfig emailConfig;

    @Override
    public void sendVerificationToken(final VerificationToken token) {
        Account account = token.getAccount();
        Locale locale = account.getLocale();
        String email = account.getAccountDetails().getEmail();

        emailSenderProvider.sendEmail(
                email,
                i18n.getMessage(VERIFICATION_MAIL_TOPIC, locale),
                i18n.getMessage(VERIFICATION_MAIL_BODY, locale)
                        + emailConfig.getAccountConfirmationUrl() + "?token=" + token.getToken()
        );
    }

    @Override
    public void sendResetToken(final VerificationToken token) {
        Account account = token.getAccount();
        Locale locale = account.getLocale();
        String email = account.getAccountDetails().getEmail();

        emailSenderProvider.sendEmail(
                email,
                i18n.getMessage(RESET_PASSWORD_MAIL_TOPIC, locale),
                i18n.getMessage(RESET_PASSWORD_MAIL_BODY, locale)
                        + emailConfig.getPasswordResetUrl() + "?token=" + token.getToken()
        );
    }
}

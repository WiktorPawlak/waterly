package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ACCOUNT_DELETED_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ACCOUNT_DELETED_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ACCOUNT_REJECTED_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ACCOUNT_REJECTED_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ACCOUNT_VERIFIED_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ACCOUNT_VERIFIED_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ADMIN_LOGIN_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ADMIN_LOGIN_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.BLOCK_ACCOUNT_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.BLOCK_ACCOUNT_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ROLE_GRANT_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ROLE_GRANT_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ROLE_REVOKE_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.ROLE_REVOKE_NOTIFICATION_MAIL_TOPIC;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.UNLOCK_ACCOUNT_NOTIFICATION_MAIL_BODY;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProviderImpl.UNLOCK_ACCOUNT_NOTIFICATION_MAIL_TOPIC;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.i18n.I18nProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender.EmailSenderProvider;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmailNotificationsImpl implements NotificationsProvider {

    @Inject
    private EmailSenderProvider emailSenderProvider;
    @Inject
    private I18nProvider i18n;

    @Override
    public void notifyAccountActiveStatusChanged(final Account account, final boolean active) {

        final String subject = active ? i18n.getMessage(UNLOCK_ACCOUNT_NOTIFICATION_MAIL_TOPIC, account.getLocale()) :
                i18n.getMessage(BLOCK_ACCOUNT_NOTIFICATION_MAIL_TOPIC, account.getLocale());
        final String body = active ? i18n.getMessage(UNLOCK_ACCOUNT_NOTIFICATION_MAIL_BODY, account.getLocale()) :
                i18n.getMessage(BLOCK_ACCOUNT_NOTIFICATION_MAIL_BODY, account.getLocale());

        emailSenderProvider.sendEmail(account.getAccountDetails().getEmail(), subject, body);
    }

    @Override
    public void notifySuccessfulAdminAuthentication(final LocalDateTime authenticationDate, final Account account, final String ipAddress) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        final String subject = i18n.getMessage(ADMIN_LOGIN_NOTIFICATION_MAIL_TOPIC, account.getLocale());
        final String body = i18n.getMessage(ADMIN_LOGIN_NOTIFICATION_MAIL_BODY,
                account.getLocale()) + authenticationDate.toLocalDate() + ", " + authenticationDate.toLocalTime().format(formatter) + "\n" +
                "Login: " + account.getLogin() + "\n" +
                "Ip: " + ipAddress;

        emailSenderProvider.sendEmail(account.getAccountDetails().getEmail(), subject, body);
    }

    @Override
    public void notifyRoleGranted(final Account account, final Set<String> role) {
        notifyRole(account, role, ROLE_GRANT_NOTIFICATION_MAIL_TOPIC, ROLE_GRANT_NOTIFICATION_MAIL_BODY);
    }

    @Override
    public void notifyRoleRevoked(final Account account, final Set<String> roleToRevoke) {
        notifyRole(account, roleToRevoke, ROLE_REVOKE_NOTIFICATION_MAIL_TOPIC, ROLE_REVOKE_NOTIFICATION_MAIL_BODY);
    }

    @Override
    public void notifyAccountRejected(final Account account) {
        sendBasicEmail(account, ACCOUNT_REJECTED_NOTIFICATION_MAIL_TOPIC, ACCOUNT_REJECTED_NOTIFICATION_MAIL_BODY);
    }

    @Override
    public void notifyAccountDeleted(final Account account) {
        sendBasicEmail(account, ACCOUNT_DELETED_NOTIFICATION_MAIL_TOPIC, ACCOUNT_DELETED_NOTIFICATION_MAIL_BODY);
    }

    @Override
    public void notifyAccountVerified(final Account account) {
        sendBasicEmail(account, ACCOUNT_VERIFIED_NOTIFICATION_MAIL_TOPIC, ACCOUNT_VERIFIED_NOTIFICATION_MAIL_BODY);
    }

    private void sendBasicEmail(final Account account, final String topicMessage, final String bodyMessage) {
        final String subject = i18n.getMessage(topicMessage, account.getLocale());
        final String body = i18n.getMessage(bodyMessage, account.getLocale());

        emailSenderProvider.sendEmail(account.getAccountDetails().getEmail(), subject, body);
    }

    private void notifyRole(final Account account, final Set<String> roleToRevoke, final String roleRevokeNotificationMailTopic,
                            final String roleRevokeNotificationMailBody) {

        final String subject = i18n.getMessage(roleRevokeNotificationMailTopic, account.getLocale());
        final String body = i18n.getMessage(roleRevokeNotificationMailBody, account.getLocale()) + String.join(", ",
                roleToRevoke.stream().map(it -> i18n.getMessage(it.toLowerCase(), account.getLocale())).toList());

        emailSenderProvider.sendEmail(account.getAccountDetails().getEmail(), subject, body);
    }
}
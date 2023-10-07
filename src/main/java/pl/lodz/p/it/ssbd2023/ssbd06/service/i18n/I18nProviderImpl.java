package pl.lodz.p.it.ssbd2023.ssbd06.service.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class I18nProviderImpl implements I18nProvider {

    public static final String VERIFICATION_MAIL_TOPIC = "mail.verification.topic";
    public static final String VERIFICATION_MAIL_BODY = "mail.verification.body";
    public static final String RESET_PASSWORD_MAIL_TOPIC = "mail.reset-password.topic";
    public static final String RESET_PASSWORD_MAIL_BODY = "mail.reset-password.body";
    public static final String CHANGE_PASSWORD_MAIL_TOPIC = "mail.change-password.topic";
    public static final String CHANGE_PASSWORD_MAIL_BODY = "mail.change-password.body";
    public static final String ACCOUNT_DETAILS_ACCEPT_MAIL_TOPIC = "mail.accept-account-details.topic";
    public static final String ACCOUNT_DETAILS_ACCEPT_MAIL_BODY = "mail.accept-account-details.body";
    public static final String UNLOCK_ACCOUNT_NOTIFICATION_MAIL_TOPIC = "mail.notification.unlock-account.topic";
    public static final String UNLOCK_ACCOUNT_NOTIFICATION_MAIL_BODY = "mail.notification.unlock-account.body";
    public static final String BLOCK_ACCOUNT_NOTIFICATION_MAIL_TOPIC = "mail.notification.block-account.topic";
    public static final String BLOCK_ACCOUNT_NOTIFICATION_MAIL_BODY = "mail.notification.block-account.body";
    public static final String ADMIN_LOGIN_NOTIFICATION_MAIL_TOPIC = "mail.notification.admin-login.topic";
    public static final String ADMIN_LOGIN_NOTIFICATION_MAIL_BODY = "mail.notification.admin-login.body";
    public static final String ROLE_GRANT_NOTIFICATION_MAIL_TOPIC = "mail.notification.role-grant.topic";
    public static final String ROLE_GRANT_NOTIFICATION_MAIL_BODY = "mail.notification.role-grant.body";
    public static final String ROLE_REVOKE_NOTIFICATION_MAIL_TOPIC = "mail.notification.role-revoke.topic";
    public static final String ROLE_REVOKE_NOTIFICATION_MAIL_BODY = "mail.notification.role-revoke.body";
    public static final String ACCOUNT_REJECTED_NOTIFICATION_MAIL_TOPIC = "mail.notification.account-rejected.topic";
    public static final String ACCOUNT_REJECTED_NOTIFICATION_MAIL_BODY = "mail.notification.account-rejected.body";
    public static final String ACCOUNT_ACCEPTED_NOTIFICATION_MAIL_TOPIC = "mail.notification.account-accepted.topic";
    public static final String ACCOUNT_ACCEPTED_NOTIFICATION_MAIL_BODY = "mail.notification.account-accepted.body";
    public static final String ACCOUNT_DELETED_NOTIFICATION_MAIL_TOPIC = "mail.notification.account-deleted.topic";
    public static final String ACCOUNT_DELETED_NOTIFICATION_MAIL_BODY = "mail.notification.account-deleted.body";
    public static final String ACCOUNT_VERIFIED_NOTIFICATION_MAIL_TOPIC = "mail.notification.account-verified.topic";
    public static final String ACCOUNT_VERIFIED_NOTIFICATION_MAIL_BODY = "mail.notification.account-verified.body";
    public static final String TWO_FA_VERIFICATION_MAIL_TOPIC = "mail.two.fa.verification.topic";
    public static final String TWO_FA_VERIFICATION_MAIL_BODY = "mail.two.fa.verification.body";

    public String getMessage(final String key, final Locale locale) {
        return getResourceBundle(locale).getString(key);
    }

    private ResourceBundle getResourceBundle(final Locale locale) {
        return ResourceBundle.getBundle("i18n/messages", locale);
    }

}

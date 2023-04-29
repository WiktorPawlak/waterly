package pl.lodz.p.it.ssbd2023.ssbd06.service.i18n;

import java.util.Locale;

public interface I18nProvider {
    String getMessage(String key, Locale locale);
}

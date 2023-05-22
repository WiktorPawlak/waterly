package pl.lodz.p.it.ssbd2023.ssbd06.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Singleton;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@Singleton
public class PropertiesHolder {

    public static final String ENVIRONMENT_KEY = "SSBD_ENVIRONMENT";
    private final Logger log = Logger.getLogger(PropertiesHolder.class.getName());

    private Properties properties;

    private String environment;

    @Property
    @Produces
    public String produceString(final InjectionPoint ip) {
        return this.properties.getProperty(getKey(ip));
    }

    @Property
    @Produces
    public int produceInt(final InjectionPoint ip) {
        return Integer.parseInt(this.properties.getProperty(getKey(ip)));
    }

    @Property
    @Produces
    public float produceFloat(final InjectionPoint ip) {
        return Float.parseFloat(this.properties.getProperty(getKey(ip)));
    }

    @Property
    @Produces
    public boolean produceBoolean(final InjectionPoint ip) {
        return Boolean.parseBoolean(this.properties.getProperty(getKey(ip)));
    }

    private String getKey(final InjectionPoint ip) {
        return (ip.getAnnotated().isAnnotationPresent(Property.class) && !ip.getAnnotated().getAnnotation(Property.class).value().isEmpty()) ?
                ip.getAnnotated().getAnnotation(Property.class).value() : ip.getMember().getName();
    }

    @PostConstruct
    public void init() {
        try {
            environment = System.getenv(ENVIRONMENT_KEY);
            if (environment == null) {
                handleFallback();
            }
        } catch (final Exception e) {
            handleFallback();
        }

        this.properties = new Properties();
        final String propertiesFile = "/application-" + environment.toLowerCase() + ".properties";
        final InputStream stream = PropertiesHolder.class.getResourceAsStream(propertiesFile);
        if (stream == null) {
            log.severe("Properties file not found");
            throw ApplicationBaseException.generalErrorException();
        }
        try {
            this.properties.load(stream);
        } catch (final IOException e) {
            log.severe("Properties could not be loaded");
            throw ApplicationBaseException.generalErrorException(e);
        }
    }

    private void handleFallback() {
        log.severe("Environment not specified, performing fallback to dev environment");
        environment = "dev";
    }
}

package pl.lodz.p.it.ssbd2023.ssbd06.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@ApplicationScoped
public class PropertiesHolder {

    private final Logger log = Logger.getLogger(PropertiesHolder.class.getName());

    private Properties properties;

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
        this.properties = new Properties();
        final InputStream stream = PropertiesHolder.class.getResourceAsStream("/application.properties");
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
}

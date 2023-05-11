package pl.lodz.p.it.ssbd2023.ssbd06.arquillian.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Startup;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
@DataSourceDefinition(
        name = "java:app/jdbc/H2DS",
        className = "org.h2.jdbcx.JdbcDataSource",
        url = "jdbc:h2:mem:test",
        user = "sa",
        password = ""
)
public class DataSourceConfiguration {

    @PersistenceContext(unitName = "adminPU")
    private EntityManager em;

}

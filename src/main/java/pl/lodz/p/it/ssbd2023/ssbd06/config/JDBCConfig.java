package pl.lodz.p.it.ssbd2023.ssbd06.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataSourceDefinition(name = "java:app/jdbc/ssbd06admin",
        className = "org.postgresql.ds.PGSimpleDataSource",
        url = "jdbc:postgresql://10.31.206.4:5432/ssbd06",
        user = "ssbd06admin",
        password = "admin12345"
)
@Stateless
public class JDBCConfig {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

}

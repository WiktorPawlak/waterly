package pl.lodz.p.it.ssbd2023.ssbd06.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataSourceDefinition(name = "java:app/jdbc/ssbd06admin",
        className = "org.postgresql.ds.PGSimpleDataSource",
        url = "${ENV=DB_URL:jdbc:postgresql://localhost:5432/ssbd06}",
        user = "ssbd06admin",
        password = "${ENV=DB_ADMIN_PASSWORD:12345}"
)
@DataSourceDefinition(name = "java:app/jdbc/ssbd06mok",
        className = "org.postgresql.ds.PGSimpleDataSource",
        url = "${ENV=DB_URL:jdbc:postgresql://localhost:5432/ssbd06}",
        user = "ssbd06mok",
        password = "${ENV=DB_MOK_PASSWORD:12345}"
)
@DataSourceDefinition(name = "java:app/jdbc/ssbd06auth",
        className = "org.postgresql.ds.PGSimpleDataSource",
        url = "${ENV=DB_URL:jdbc:postgresql://localhost:5432/ssbd06}",
        user = "ssbd06auth",
        password = "${ENV=DB_AUTH_PASSWORD:12345}"
)
@DataSourceDefinition(name = "java:app/jdbc/ssbd06mol",
        className = "org.postgresql.ds.PGSimpleDataSource",
        url = "${ENV=DB_URL:jdbc:postgresql://localhost:5432/ssbd06}",
        user = "ssbd06mol",
        password = "${ENV=DB_MOL_PASSWORD:12345}"
)
@Stateless
public class JDBCConfig {

    @PersistenceContext(unitName = "adminPU")
    private EntityManager em;

}

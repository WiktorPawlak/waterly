package pl.lodz.p.it.ssbd2023.ssbd06.arquillian.config;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers.AccountVerificationStartup;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.config.JDBCConfig;


@ExtendWith(ArquillianExtension.class)
public class BaseArquillianTest {

    @Inject
    protected UserTransaction userTransaction;

    @Resource(lookup = "java:app/jdbc/H2DS")
    private DataSource dataSource;

    @Deployment
    public static WebArchive createDeployment() {
        File[] libraries = Maven.configureResolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(libraries)
                .addPackages(true, Filters.exclude(JDBCConfig.class, AccountVerificationStartup.class), "pl.lodz.p.it.ssbd2023.ssbd06")
                .addAsResource(new File("src/test/resources/test.properties"), "application.properties")
                .addAsResource(new File("src/main/resources/i18n"), "i18n")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("test-beans.xml", "beans.xml");
    }


    @AfterEach
    @SneakyThrows
    public void clearDatabase() {
        truncateTables();
    }


    private void truncateTables() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            Statement s = c.createStatement();

            // Disable FK
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Find all tables and truncate them
            Set<String> tables = new HashSet<>();
            ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            rs.close();
            for (String table : tables) {
                s.executeUpdate("TRUNCATE TABLE " + table);
            }

            // Idem for sequences
            Set<String> sequences = new HashSet<>();
            rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                sequences.add(rs.getString(1));
            }
            rs.close();
            for (String seq : sequences) {
                s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
            }

            // Enable FK
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            s.close();
        }
    }
}
package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MariaDBTestResource implements QuarkusTestResourceLifecycleManager {

    MariaDBContainer mariaDB;
    private static final Network network = Network.newNetwork();
    private static final String DB_NAME = "ssbd06";
    private static final String DB_USERNAME = "ssbd06admin";
    private static final String DB_PASSWORD = "12345";
    private static final DockerImageName MARIADB_IMAGE = DockerImageName
            .parse("mariadb")
            .withTag("11.1");

    @Override
    public Map<String, String> start() {
        mariaDB = new MariaDBContainer<>(MARIADB_IMAGE)
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USERNAME)
                .withPassword(DB_PASSWORD)
                .withNetwork(network)
                .withEnv("MYSQL_LOG_CONSOLE", "1")
                .withCopyFileToContainer(MountableFile.
                        forClasspathResource("docker-init.sql"), "/docker-entrypoint-initdb.d/init.sql")
                .withNetworkAliases("mariadb")
                .waitingFor(new HostPortWaitStrategy());
        mariaDB.start();
        Map<String, String> conf = new HashMap<>();
        conf.put("quarkus.datasource.jdbc.url", mariaDB.getJdbcUrl());
        conf.put("quarkus.datasource.jdbc.first-port", String.valueOf(mariaDB.getFirstMappedPort()));
        conf.put("quarkus.datasource.username", mariaDB.getUsername());
        conf.put("quarkus.datasource.password", mariaDB.getPassword());
        conf.put("quarkus.hibernate-orm.database.generation", "drop-and-create");

        conf.put("quarkus.datasource.ssbd06mol.jdbc.url", "jdbc:mariadb://localhost:" + mariaDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        conf.put("quarkus.datasource.ssbd06mok.jdbc.url", "jdbc:mariadb://localhost:" + mariaDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        conf.put("quarkus.datasource.ssbd06admin.jdbc.url", "jdbc:mariadb://localhost:" + mariaDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        conf.put("quarkus.datasource.ssbd06auth.jdbc.url", "jdbc:mariadb://localhost:" + mariaDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        return conf;
    }

    @Override
    public void stop() {
        mariaDB.stop();
    }
}
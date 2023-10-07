package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresDBTestResource implements QuarkusTestResourceLifecycleManager {

    PostgreSQLContainer postgresDB;
    private static final Network network = Network.newNetwork();
    private static final String DB_NAME = "postgres";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName
            .parse("postgres")
            .withTag("15.2-alpine");

    @Override
    public Map<String, String> start() {
        postgresDB = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USERNAME)
                .withPassword(DB_PASSWORD)
                .withNetwork(network)
                .withCopyFileToContainer(MountableFile.
                        forClasspathResource("docker-init.sql"), "/docker-entrypoint-initdb.d/init.sql")
                .withNetworkAliases("postgres")
                .waitingFor(new HostPortWaitStrategy());
        postgresDB.start();
        Map<String, String> conf = new HashMap<>();
        conf.put("quarkus.datasource.jdbc.url", postgresDB.getJdbcUrl());
        conf.put("quarkus.datasource.jdbc.first-port", String.valueOf(postgresDB.getFirstMappedPort()));
        conf.put("quarkus.datasource.username", postgresDB.getUsername());
        conf.put("quarkus.datasource.password", postgresDB.getPassword());


        conf.put("quarkus.datasource.ssbd06mol.jdbc.url", "jdbc:postgresql://localhost:" + postgresDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        conf.put("quarkus.datasource.ssbd06mok.jdbc.url", "jdbc:postgresql://localhost:" + postgresDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        conf.put("quarkus.datasource.ssbd06admin.jdbc.url", "jdbc:postgresql://localhost:" + postgresDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
        conf.put("quarkus.datasource.ssbd06auth.jdbc.url", "jdbc:postgresql://localhost:" + postgresDB.getFirstMappedPort() + "/ssbd06?loggerLevel=OFF");
//        conf.put("quarkus.hibernate-orm.\"adminPU\".database.generation", "drop-and-create");
//        conf.put("quarkus.hibernate-orm.\"adminPU\".sql-load-script", "test-init.sql");
//        conf.put("DB_ADMIN_PASSWORD", "12345");
//        conf.put("DB_MOK_PASSWORD", "12345");
//        conf.put("DB_AUTH_PASSWORD", "12345");
//        conf.put("DB_MOL_PASSWORD", "12345");
//        conf.put("DB_URL", "jdbc:postgresql://postgres:" + postgresDB.getFirstMappedPort() + "/ssbd06");
        return conf;
    }

    private String fixJdbcUrl(String jdbcUrl) {
        // Part of the JDBC URL to replace
        String hostPort = postgresDB.getHost() + ':' + postgresDB.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);

        // Host/IP on the container network plus the unmapped port
        String networkHostPort =
                postgresDB.getCurrentContainerInfo().getConfig().getHostName()
                        + ':'
                        + PostgreSQLContainer.POSTGRESQL_PORT;

        return jdbcUrl.replace(hostPort, networkHostPort);
    }

    @Override
    public void stop() {
        postgresDB.stop();
    }
}
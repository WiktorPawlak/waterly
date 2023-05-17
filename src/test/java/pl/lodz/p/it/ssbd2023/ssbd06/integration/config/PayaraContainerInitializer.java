package pl.lodz.p.it.ssbd2023.ssbd06.integration.config;

import static io.restassured.http.ContentType.JSON;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PayaraContainerInitializer {

    private static final String APP_NAME = "ssbd06-0.2.1";

    private static final String DB_NAME = "postgres";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName
            .parse("postgres")
            .withTag("15.2-alpine");

    private static final int PORT = 8080;
    private static final String PACKAGE_NAME = APP_NAME + ".war";
    private static final String CONTAINER_DEPLOYMENT_PATH = "/opt/payara/deployments/";
    private static final DockerImageName PAYARA_IMAGE = DockerImageName
            .parse("payara/server-full")
            .withTag("6.2023.3-jdk17");
    private static final Network network = Network.newNetwork();
    private static final Map<String, String> PAYARA_ENVS = Map.of(
            "DB_ADMIN_PASSWORD", "12345",
            "DB_MOK_PASSWORD", "12345",
            "DB_AUTH_PASSWORD", "12345",
            "DB_MOL_PASSWORD", "12345",
            "DB_URL", "jdbc:postgresql://postgres:5432/ssbd06"
    );

    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName(DB_NAME)
                    .withUsername(DB_USERNAME)
                    .withPassword(DB_PASSWORD)
                    .withNetwork(network)
                    .withCopyFileToContainer(MountableFile.
                            forClasspathResource("docker-init.sql"), "/docker-entrypoint-initdb.d/init.sql")
                    .withNetworkAliases("postgres")
                    .waitingFor(new HostPortWaitStrategy());

    protected static GenericContainer<?> payaraServer = new GenericContainer<>(PAYARA_IMAGE)
            .withExposedPorts(PORT)
            .withCopyFileToContainer(MountableFile.forHostPath("target/" + PACKAGE_NAME), CONTAINER_DEPLOYMENT_PATH + PACKAGE_NAME)
            .withNetwork(network)
            .withEnv(PAYARA_ENVS)
            .dependsOn(postgres)
            .waitingFor(Wait.forHttp("/api/health").forPort(PORT).forStatusCode(200));

    @BeforeAll
    protected void setup() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        if (!payaraServer.isRunning()) {
            payaraServer.start();
        }

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBasePath("/api")
                .setPort(payaraServer.getMappedPort(PORT))
                .setContentType(JSON)
                .build();
    }
}

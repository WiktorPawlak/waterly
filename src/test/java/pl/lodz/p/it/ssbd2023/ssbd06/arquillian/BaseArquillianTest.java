package pl.lodz.p.it.ssbd2023.ssbd06.arquillian;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;


@ExtendWith(ArquillianExtension.class)
public class BaseArquillianTest {

    @Inject
    protected UserTransaction userTransaction;

    @Deployment
    public static WebArchive createDeployment() {
        File[] libraries = Maven.configureResolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(libraries)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd06")
                .addAsResource(new File("src/test/resources/test.properties"), "application.properties")
                .addAsResource(new File("src/main/resources/i18n"), "i18n")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}
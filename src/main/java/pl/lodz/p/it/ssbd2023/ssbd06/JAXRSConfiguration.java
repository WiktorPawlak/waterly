package pl.lodz.p.it.ssbd2023.ssbd06;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@QuarkusMain
@ApplicationPath("/api")
@DeclareRoles({ADMINISTRATOR, OWNER, FACILITY_MANAGER})
public class JAXRSConfiguration extends Application {

    public static void main(String ... args) {
        Quarkus.run(args);
    }

}
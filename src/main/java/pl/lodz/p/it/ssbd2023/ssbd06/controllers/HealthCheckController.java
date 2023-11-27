package pl.lodz.p.it.ssbd2023.ssbd06.controllers;

import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
@RequestScoped
public class HealthCheckController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkHealth() {
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/exit")
    @OnlyGuest
    public Response exitApplication() {
        System.exit(999);
        return Response.ok("Exited successfully").build();
    }
}

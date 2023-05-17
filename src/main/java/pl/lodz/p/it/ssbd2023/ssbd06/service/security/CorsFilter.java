package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Inject
    @Property("cors.allow-origin")
    private String allowOrigin;

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext responseContext) throws IOException {

        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", allowOrigin);
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Expose-Headers", "Etag");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "If-Match");
    }
}
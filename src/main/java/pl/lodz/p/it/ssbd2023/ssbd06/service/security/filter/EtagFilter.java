package pl.lodz.p.it.ssbd2023.ssbd06.service.security.filter;

import java.util.Arrays;
import java.util.Optional;

import io.quarkus.arc.Arc;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.EtagValidationFilter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadSigner;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadVerifier;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.exceptions.NoPayloadException;


@EtagValidationFilter
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class EtagFilter {

    private static final String IF_MATCH_HEADER_NAME = "If-Match";

    @Inject
    PayloadVerifier payloadVerifier;

    @Inject
    PayloadSigner payloadSigner;

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        Optional<String> header = Optional.ofNullable(Arc.container()
                .requestContext()
                .getState()
                .getContextualInstances()
                .values()
                .stream()
                .filter(CurrentVertxRequest.class::isInstance)
                .map(CurrentVertxRequest.class::cast)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getCurrent()
                .request()
                .headers()
                .get(IF_MATCH_HEADER_NAME));
        header.ifPresentOrElse(optHeader -> {
            if (!payloadVerifier.verify(optHeader)) {
                throw ApplicationBaseException.jwsException();
            }
            Signable payload = Arrays.stream(context.getParameters())
                    .filter(Signable.class::isInstance)
                    .map(Signable.class::cast)
                    .findFirst()
                    .orElseThrow(NoPayloadException::new);

            if (!verifyEntityIntegrity(payload, optHeader)) {
                throw ApplicationBaseException.entityIntegrityViolatedException();
            }

        }, () -> {
            throw ApplicationBaseException.ifMatchHeaderMissingException();
        });
        return context.proceed();
    }

    private boolean verifyEntityIntegrity(final Signable payload, final String headerValue) {
        return payloadSigner.sign(payload).equals(headerValue);
    }

}
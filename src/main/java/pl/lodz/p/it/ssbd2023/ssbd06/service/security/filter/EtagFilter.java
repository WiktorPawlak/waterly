package pl.lodz.p.it.ssbd2023.ssbd06.service.security.filter;

import java.util.Arrays;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.EtagValidationFilter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadSigner;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadVerifier;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.exceptions.NoPayloadException;


@EtagValidationFilter
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class EtagFilter {

    private static final String IF_MATCH_HEADER_NAME = "If-Match";

    @Context
    HttpHeaders requestHeaders;

    @Inject
    private PayloadVerifier payloadVerifier;

    @Inject
    private PayloadSigner payloadSigner;

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        Optional<String> header = Optional.ofNullable(requestHeaders.getHeaderString(IF_MATCH_HEADER_NAME));
        header.ifPresentOrElse(optHeader -> {
            if (!payloadVerifier.verify(optHeader)) {
                throw ApplicationBaseException.jwsException();
            }
            Signable payload = Arrays.stream(context.getParameters())
                    .filter(Signable.class::isInstance)
                    .map(Signable.class::cast)
                    .findFirst()
                    .orElseThrow(NoPayloadException::new);

            if (!verifyEntityIntegrity(payload)) {
                throw ApplicationBaseException.entityIntegrityViolatedException();
            }

        }, () -> {
            throw ApplicationBaseException.ifMatchHeaderMissingException();
        });
        return context.proceed();
    }

    private boolean verifyEntityIntegrity(final Signable payload) {
        return payloadSigner.sign(payload).equals(requestHeaders.getHeaderString(IF_MATCH_HEADER_NAME));
    }

}
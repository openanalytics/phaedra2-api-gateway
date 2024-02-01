package eu.openanalytics.phaedra.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Collection;

public class PhaedraLogoutHandler extends DelegatingServerLogoutHandler {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Value("${keycloak-base-url}/auth/realms/phaedra2/protocol/openid-connect/logout")
    private String logoutURI;
    private String redirectURI = "https://phaedra.poc.openanalytics.io/phaedra/ui";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public PhaedraLogoutHandler(ServerLogoutHandler... delegates) {
        super(delegates);
    }

    public PhaedraLogoutHandler(Collection<ServerLogoutHandler> delegates) {
        super(delegates);
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        logger.info("Enter PhaedraLogoutHandler ... ");
        REST_TEMPLATE.exchange(logoutURI, HttpMethod.GET, HttpEntity.EMPTY, Void.class);
        return super.logout(exchange, authentication);
    }
}

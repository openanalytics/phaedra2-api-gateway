package eu.openanalytics.phaedra.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PhaedraLogoutHandler implements ServerLogoutHandler {
    @Value("${keycloak-base-url}/auth/realms/phaedra2/protocol/openid-connect/logout")
    private String logoutURI;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        String logoutUrl = logoutURI + "?redirect_uri=https://phaedra.poc.openanalytics.io/phaedra/ui";
        logger.info(logoutUrl);
        return null;
    }
}

package eu.openanalytics.phaedra.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@CrossOrigin
@RestController
public class LogoutController {
    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String keycloakAuthUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
    private final ServerRequestCache requestCache = new WebSessionServerRequestCache();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/userlogout")
    public Mono<Void> logout(ServerWebExchange exchange) {
        logger.info(String.format("keycloakAuthUri: %s, clientId: %s", keycloakAuthUri, clientId));
        return exchange.getSession()
                .flatMap(webSession -> {
                    webSession.invalidate();
                    return securityContextRepository.save(exchange, null);
                })
                .then(Mono.defer(() -> Mono.just(exchange.getResponse())))
                .flatMap(response -> {
                    String logoutUri = keycloakAuthUri.replace("/auth", "/protocol/openid-connect/logout")
                            +"?redirect_uri=" + exchange.getRequest().getURI();
                    response.getHeaders().setLocation(URI.create(logoutUri));
                    return response.setComplete();
                });
    }
}

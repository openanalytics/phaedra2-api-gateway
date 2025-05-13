/**
 * Phaedra II
 *
 * Copyright (C) 2016-2025 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.phaedra.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
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

    @Value("${keycloak-logout-url}")
    private String keycloakLogoutUrl;

    @Value("${redirect-uri}")
    private String redirectUri;

    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
    private final ServerRequestCache requestCache = new WebSessionServerRequestCache();
    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

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
                .then(redirectStrategy.sendRedirect(exchange, createLogoutUri(exchange)));
    }

    private URI createLogoutUri(ServerWebExchange exchange) {
        String logoutUri = String.format(keycloakLogoutUrl + "?post_logout_redirect_uri=%s&client_id=%s", redirectUri, clientId);
        return URI.create(logoutUri);
    }
}

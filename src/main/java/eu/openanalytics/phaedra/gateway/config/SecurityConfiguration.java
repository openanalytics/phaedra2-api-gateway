package eu.openanalytics.phaedra.gateway.config;

import eu.openanalytics.phaedra.gateway.PhaedraLogoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.*;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    private String logoutURI = "https://keycloak.phaedra.poc.openanalytics.io/auth/realms/phaedra2/protocol/openid-connect/logout";
    private String redirectURI = "https://phaedra.poc.openanalytics.io/phaedra/ui";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public SecurityConfiguration(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                // API requests are routed freely (for now), the endpoint may choose to deny the request.
                .pathMatchers("/api/**").permitAll()
                // The userinfo endpoint is accessible freely. Without an authenticated session, there is nothing to see.
                .pathMatchers("/userinfo").permitAll()
                .pathMatchers("/oauth2/logout").permitAll()
                // The Swagger UI pages is accessible freely (for now)
//                .pathMatchers("/*/swagger-ui.html").permitAll()
//                .pathMatchers("/*/swagger-ui/**").permitAll()
//                .pathMatchers("/v3/api-docs/**").permitAll()
                // GraphQL related endpoints are routed freely (for now)
                .pathMatchers("/graphiql").permitAll()
                .pathMatchers("/graphql").permitAll()
                // The remaining requests, i.e. UI requests, must follow the OAuth2 authorization flow
                .anyExchange().authenticated()
                .and().oauth2Login()
                .and().csrf().disable()
                .build();
    }

    private ServerLogoutHandler logoutHandler() {
        return new PhaedraLogoutHandler(new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler());
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        logger.info("Creating ServerLogoutSuccessHandler ...");
        OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri("https://phaedra.poc.openanalytics.io/phaedra/ui");
        return logoutSuccessHandler;
    }
}

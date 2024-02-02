package eu.openanalytics.phaedra.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                // API requests are routed freely (for now), the endpoint may choose to deny the request.
                .pathMatchers("/api/**").permitAll()
                // The userinfo endpoint is accessible freely. Without an authenticated session, there is nothing to see.
                .pathMatchers("/userinfo").permitAll()
                .pathMatchers("/userlogout").permitAll()
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
}

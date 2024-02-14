/**
 * Phaedra II
 *
 * Copyright (C) 2016-2024 Open Analytics
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

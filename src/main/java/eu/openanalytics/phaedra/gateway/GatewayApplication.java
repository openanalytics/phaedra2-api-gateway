package eu.openanalytics.phaedra.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * https://spring.io/blog/2019/08/16/securing-services-with-spring-cloud-gateway
 *
 * https://developer.okta.com/blog/2020/08/14/spring-gateway-patterns#pattern-1-openid-connect-authentication
 */

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GatewayApplication.class);
		app.run(args);
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange()
				// API requests are routed freely (for now), the endpoint may choose to deny the request.
				.pathMatchers("/api/**").permitAll()
				// The userinfo endpoint is accessible freely. Without an authenticated session, there is nothing to see.
				.pathMatchers("/userinfo").permitAll()
				// The remaining requests, i.e. UI requests, must follow the OAuth2 authorization flow
				.anyExchange().authenticated()
			.and().oauth2Login()
			.and().csrf().disable()
			.build();
	}
}

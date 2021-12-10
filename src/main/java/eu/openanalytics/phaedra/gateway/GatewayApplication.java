package eu.openanalytics.phaedra.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}

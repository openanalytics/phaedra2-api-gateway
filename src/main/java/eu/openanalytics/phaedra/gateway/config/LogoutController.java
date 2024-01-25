package eu.openanalytics.phaedra.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
public class LogoutController {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Value("${keycloak-base-url}/auth/realms/phaedra2/protocol/openid-connect/logout")
    private String logoutURI;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout() {
        logger.info("Enter logout service");
        logger.info("Keycloak logout uri: " + logoutURI);
        REST_TEMPLATE.exchange(logoutURI, HttpMethod.POST, null, String.class);
    }
}

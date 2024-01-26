package eu.openanalytics.phaedra.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CrossOrigin
@RestController
public class LogoutController {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Value("${keycloak-base-url}/auth/realms/phaedra2/protocol/openid-connect/logout")
    private String logoutURI;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value ="/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        String logoutUrl = logoutURI + "?redirect_uri=https://phaedra.poc.openanalytics.io/phaedra/ui";
        logger.info(logoutUrl);
        response.sendRedirect(logoutUrl);
    }
}

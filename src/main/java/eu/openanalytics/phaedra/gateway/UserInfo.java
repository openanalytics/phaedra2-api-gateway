package eu.openanalytics.phaedra.gateway;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class UserInfo {

	@RequestMapping(value = "/userinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserInfo(Principal principal) {
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
		
		if (token != null && token.getPrincipal() instanceof OidcUser) {
			OidcUser user = (OidcUser) token.getPrincipal();
	        return ResponseEntity.ok(user.getUserInfo());
		}
		
		return ResponseEntity.notFound().build();
    }
}

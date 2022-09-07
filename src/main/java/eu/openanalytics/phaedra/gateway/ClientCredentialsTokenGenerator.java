package eu.openanalytics.phaedra.gateway;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ClientCredentialsTokenGenerator {

	private DefaultReactiveOAuth2AuthorizedClientManager clientManager;
	
	@Value("${gateway.user-lookup.client-reg-id}")
	private String clientRegId;
	
	@Autowired
	private ReactiveClientRegistrationRepository clientRegistrationRepository;
	
	@Autowired
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;
	
	@PostConstruct
	public void init() {
		ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

		clientManager = new DefaultReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
		clientManager.setAuthorizedClientProvider(authorizedClientProvider);
	}
			
	public Mono<OAuth2AccessToken> obtainToken() {
		OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
				.withClientRegistrationId(clientRegId)
				.principal("GatewayClient")
				.build();
		
		return clientManager.authorize(authorizeRequest).map(client -> client.getAccessToken());
	}
}

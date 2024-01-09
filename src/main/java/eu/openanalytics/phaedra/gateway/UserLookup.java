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
package eu.openanalytics.phaedra.gateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * Notes:
 * - Not using the principal's auth here, but instead a client credentials flow.
 * - Therefore, the client (phaedra2-gateway) needs the service account role: realm-management > view-users
 */
@CrossOrigin
@RestController
public class UserLookup {

	private static final RestTemplate REST_TEMPLATE = new RestTemplate();
	
	@Autowired
	private ClientCredentialsTokenGenerator tokenGenerator;
	
	@Value("${gateway.user-lookup.endpoint}")
	private String endpointURL;
	
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,String>> getUsers() {
		return tokenGenerator.obtainToken()
			.map(token -> {
				// Make a request to the Keycloak Admin API to fetch a list of users.
				HttpHeaders headers = new HttpHeaders();
				headers.add("Authorization", "Bearer " + token.getTokenValue());
		        HttpEntity<?> request = new HttpEntity<>(headers);
				ResponseEntity<String> response = REST_TEMPLATE.exchange(endpointURL, HttpMethod.GET, request, String.class);
				String body = response.getBody();
				
				Map<String,String> users = new HashMap<>();
				
				ObjectMapper mapper = new ObjectMapper();
				try {
					@SuppressWarnings("unchecked")
					List<Map<String,Object>> userInfos = mapper.readValue(body, List.class);
					userInfos.stream().forEach(info -> {
						users.put(info.get("id").toString(), info.get("username").toString());
					});
				} catch (Exception e) {
					throw new RuntimeException("Failed to parse user information", e);
				}
				
				return users;
		});
	}
}

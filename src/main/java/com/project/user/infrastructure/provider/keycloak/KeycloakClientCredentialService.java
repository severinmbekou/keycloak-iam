package com.project.user.infrastructure.provider.keycloak;

import com.project.user.domain.ClientCredentialService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakClientCredentialService implements ClientCredentialService {

  private final KeycloakProperties properties;
  private final WebClient webClient;

  @Override
  public Map<String, Object> clientCredentials() {
    return webClient
        .post()
        .uri("/realms/" + properties.getRealm() + "/protocol/openid-connect/token")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .body(
            BodyInserters.fromFormData("client_id", properties.getClientId())
                .with("client_secret", properties.getClientSecret())
                .with("grant_type", "client_credentials"))
        .retrieve()
        .bodyToMono(Map.class)
        .block();
  }
}

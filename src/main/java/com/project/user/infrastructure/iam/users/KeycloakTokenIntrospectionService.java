package com.project.user.infrastructure.iam.users;

import com.project.user.domain.iam.TokenIntrospectionService;
import com.project.user.infrastructure.config.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakTokenIntrospectionService implements TokenIntrospectionService {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  @Override
  public Map<String, Object> introspect(String token) {
    return webClient
        .post()
        .uri(
            properties.getServerUrl()
                + "/realms/"
                + properties.getRealm()
                + "/protocol/openid-connect/token/introspect")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(
            BodyInserters.fromFormData("client_id", properties.getClientId())
                .with("client_secret", properties.getClientSecret())
                .with("token", token))
        .retrieve()
        .bodyToMono(Map.class)
        .block();
  }
}

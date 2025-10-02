package com.project.user.infrastructure.iam.users;

import com.project.user.domain.iam.users.UserTokenRefreshService;
import com.project.user.infrastructure.config.KeycloakProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class KeycloakUserTokenRefreshService implements UserTokenRefreshService {

  private final String tokenUrl;
  private final WebClient webClient;
  private final KeycloakProperties properties;

  public KeycloakUserTokenRefreshService(WebClient webClient, KeycloakProperties properties) {
    this.webClient = webClient;
    this.properties = properties;
    this.tokenUrl =
        properties.getServerUrl()
            + "/realms/"
            + properties.getRealm()
            + "/protocol/openid-connect/token";
  }

  @Override
  public Map<String, Object> refreshToken(String refreshToken) {
    return webClient
        .post()
        .uri(tokenUrl)
        .body(
            BodyInserters.fromFormData("grant_type", "refresh_token")
                .with("client_id", properties.getClientId())
                .with("client_secret", properties.getClientSecret())
                .with("refresh_token", refreshToken))
        .retrieve()
        .bodyToMono(Map.class)
        .doOnSuccess(res -> log.info("✅ User token refresh successfully"))
        .doOnError(error -> log.error("❌ Error refreshing user token: {}", error.getMessage()))
        .block();
  }
}

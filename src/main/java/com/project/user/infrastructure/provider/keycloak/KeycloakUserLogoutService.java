package com.project.user.infrastructure.provider.keycloak;

import com.project.user.domain.UserLogoutService;
import com.project.user.infrastructure.config.KeycloakProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserLogoutService implements UserLogoutService {
  private final String logoutUrl;
  private final WebClient webClient;
  private final KeycloakProperties properties;

  public KeycloakUserLogoutService(WebClient webClient, KeycloakProperties properties) {
    this.webClient = webClient;
    this.properties = properties;
    this.logoutUrl =
        properties.getServerUrl()
            + "/realms/"
            + properties.getRealm()
            + "/protocol/openid-connect/logout";
  }

  @Override
  public void logout(String refreshToken) {
    webClient
        .post()
        .uri(logoutUrl)
        .body(
            BodyInserters.fromFormData("client_id", properties.getClientId())
                .with("client_secret", properties.getClientSecret())
                .with("refresh_token", refreshToken))
        .retrieve()
        .toBodilessEntity()
        .doOnSuccess(res -> log.info("✅ User logout successfully "))
        .doOnError(error -> log.error("❌ Error logout user: {}", error.getMessage()))
        .block();
  }
}

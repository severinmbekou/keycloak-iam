package com.project.user.infrastructure.iam.clients;

import com.project.user.domain.iam.clients.ClientAuthService;
import com.project.user.infrastructure.config.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientAuthServiceImpl implements ClientAuthService {
  private final WebClient webClient;
  private final KeycloakProperties properties;

  @Override
  public Map<String, Object> clientLogin(String clientId, String clientSecret) {
    try {
      Map<String, Object> tokenResponse =
          webClient
              .post()
              .uri("/realms/" + properties.getRealm() + "/protocol/openid-connect/token")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
              .body(
                  BodyInserters.fromFormData("client_id", clientId)
                      .with("client_secret", clientSecret)
                      .with("grant_type", "client_credentials"))
              .retrieve()
              .bodyToMono(Map.class)
              .block();

      log.info("✅ Client {} successfully authenticated", clientId);
      return tokenResponse;

    } catch (Exception e) {
      log.error("❌ Client login failed for {}: {}", clientId, e.getMessage());
      throw new RuntimeException("Client login failed: " + e.getMessage());
    }
  }
}

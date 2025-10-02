package com.project.user.infrastructure.iam.users;

import com.project.user.domain.iam.users.UserEmailService;
import com.project.user.infrastructure.config.KeycloakProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserVerificationService extends AdminTokenResource
    implements UserEmailService {
  private final WebClient webClient;
  private final KeycloakProperties properties;

  public KeycloakUserVerificationService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.properties = properties;
  }

  @Override
  public void triggerEmailVerification(String username) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);

    webClient
        .put()
        .uri(
            properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/users/"
                + userId
                + "/send-verify-email")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  @Override
  public void sendForgotPasswordEmail(String username) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);
    webClient
        .put()
        .uri(
            properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/users/"
                + userId
                + "/execute-actions-email")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new String[] {"UPDATE_PASSWORD"})
        .retrieve()
        .toBodilessEntity()
        .block();
  }
}

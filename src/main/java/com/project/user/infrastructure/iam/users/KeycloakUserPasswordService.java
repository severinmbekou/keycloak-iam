package com.project.user.infrastructure.iam.users;

import com.project.user.application.model.ResetPasswordRequest;
import com.project.user.domain.iam.users.UserEmailService;
import com.project.user.domain.iam.users.UserPasswordService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserPasswordService extends AdminTokenResource implements UserPasswordService {

  private final KeycloakProperties properties;
  private final WebClient webClient;
  private final UserEmailService userEmailService;

  public KeycloakUserPasswordService(
      KeycloakProperties properties, WebClient webClient, UserEmailService userEmailService) {
    super(properties, webClient);
    this.properties = properties;
    this.webClient = webClient;
    this.userEmailService = userEmailService;
  }

  @Override
  public void resetPassword(ResetPasswordRequest request) {
    String token = getAdminAccessToken();
    userEmailService.sendForgotPasswordEmail(request.username());
    // Find user
    String userId =
        webClient
            .get()
            .uri("/admin/realms/" + properties.getRealm() + "/users?username=" + request.username())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToFlux(Map.class)
            .blockFirst()
            .get("id")
            .toString();

    Map<String, Object> cred =
        Map.of("type", "password", "value", request.newPassword(), "temporary", false);

    webClient
        .put()
        .uri("/admin/realms/" + properties.getRealm() + "/users/" + userId + "/reset-password")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cred)
        .retrieve()
        .toBodilessEntity()
        .block();
  }
}

package com.project.user.infrastructure.iam.users;

import com.project.user.application.model.ChangePasswordRequest;
import com.project.user.domain.iam.users.UserSelfPasswordService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserSelfPasswordService extends AdminTokenResource
    implements UserSelfPasswordService {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  public KeycloakUserSelfPasswordService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.properties = properties;
    this.webClient = webClient;
  }

  @Override
  public void changePassword(String username, ChangePasswordRequest request) {

    String token = getAdminAccessToken();
    String userId = getUserId(username, token);
    Map<String, Object> cred =
        Map.of("type", "password", "value", request.newPassword(), "temporary", false);

    webClient
        .put()
        .uri(
            properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/users/"
                + userId
                + "/reset-password")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cred)
        .retrieve()
        .toBodilessEntity()
        .block();
  }
}

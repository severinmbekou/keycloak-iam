package com.project.user.infrastructure.provider.keycloak;

import com.project.user.domain.UserAccountService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserAccountService extends AdminTokenResource implements UserAccountService {
  private final KeycloakProperties properties;
  private final WebClient webClient;
  private final String baseUserUrl;

  public KeycloakUserAccountService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.properties = properties;
    this.webClient = webClient;
    this.baseUserUrl =
        properties.getServerUrl() + "/admin/realms/" + properties.getRealm() + "/users";
  }

  @Override
  public void disableUser(String username) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);

    Map<String, Object> update = Map.of("enabled", false);
    webClient
        .put()
        .uri(this.baseUserUrl + "/" + userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  @Override
  public void enableUser(String username) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);

    Map<String, Object> update = Map.of("enabled", true);

    webClient
        .put()
        .uri(this.baseUserUrl + "/" + userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  @Override
  public void deleteUser(String username) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);
    webClient
        .delete()
        .uri(this.baseUserUrl + "/" + userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .toBodilessEntity()
        .block();
  }
}

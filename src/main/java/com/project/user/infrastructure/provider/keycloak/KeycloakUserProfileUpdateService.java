package com.project.user.infrastructure.provider.keycloak;

import com.project.user.application.model.UpdateUserProfileRequest;
import com.project.user.domain.UserProfileUpdateService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserProfileUpdateService extends AdminTokenResource
    implements UserProfileUpdateService {
  private final WebClient webClient;
  private final String baseUserUrl;

  public KeycloakUserProfileUpdateService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.baseUserUrl =
        properties.getServerUrl() + "/admin/realms/" + properties.getRealm() + "/users";
  }

  @Override
  public void updateProfile(String username, UpdateUserProfileRequest updateRequest) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);
    Map<String, Object> updates =
        Map.of(
            "emailVerified", updateRequest.emailVerified(),
            "firstName", updateRequest.firstName(),
            "lastName", updateRequest.lastName());
    webClient
        .put()
        .uri(this.baseUserUrl + "/" + userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updates)
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  @Override
  public Map<String, Object> getProfile(String username) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);
    return webClient
        .get()
        .uri(this.baseUserUrl + "/" + userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .bodyToMono(Map.class)
        .block();
  }
}

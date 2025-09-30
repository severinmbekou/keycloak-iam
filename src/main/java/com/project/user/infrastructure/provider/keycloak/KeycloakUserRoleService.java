package com.project.user.infrastructure.provider.keycloak;

import com.project.user.domain.UserRoleService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserRoleService extends AdminTokenResource implements UserRoleService {
  private final WebClient webClient;

  private final String baseUserUrl;
  private final String baseRoleUrl;

  public KeycloakUserRoleService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.baseUserUrl =
        properties.getServerUrl() + "/admin/realms/" + properties.getRealm() + "/users";
    this.baseRoleUrl =
        properties.getServerUrl() + "/admin/realms/" + properties.getRealm() + "/roles/";
  }

  @Override
  public void assignRole(String username, String roleName) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);

    Map<String, Object> role =
        webClient
            .get()
            .uri(this.baseRoleUrl + roleName)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    webClient
        .post()
        .uri(this.baseUserUrl + "/" + userId + "/role-mappings/realm")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(List.of(role))
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  @Override
  public void removeRole(String username, String roleName) {
    String token = getAdminAccessToken();
    String userId = getUserId(username, token);

    Map<String, Object> role =
        webClient
            .get()
            .uri(this.baseRoleUrl + roleName)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    webClient
        .method(HttpMethod.DELETE)
        .uri(this.baseUserUrl + "/" + userId + "/role-mappings/realm")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(List.of(role))
        .retrieve()
        .toBodilessEntity()
        .block();
  }
}

package com.project.user.infrastructure;

import com.project.user.domain.UserQueryService;
import com.project.user.infrastructure.config.KeycloakProperties;
import com.project.user.infrastructure.provider.keycloak.AdminTokenResource;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloackUserQueryService extends AdminTokenResource implements UserQueryService {
  private final KeycloakProperties properties;
  private final WebClient webClient;
  private final String baseUserUrl;

  public KeycloackUserQueryService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.properties = properties;
    this.webClient = webClient;
    this.baseUserUrl = "/admin/realms/" + properties.getRealm() + "/users";
  }

  @Override
  public List<Map<String, Object>> listAllUsers(int first, int max) {
    return webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(baseUserUrl)
                    .queryParam("first", first)
                    .queryParam("max", max)
                    .build())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminAccessToken())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(Map.class)
        .map(m -> (Map<String, Object>) m)
        .collectList()
        .block();
  }

  @Override
  public Map<String, Object> getUserByUsername(String username) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(baseUserUrl).queryParam("username", username).build())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminAccessToken())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(Map.class)
        .blockFirst();
  }
}

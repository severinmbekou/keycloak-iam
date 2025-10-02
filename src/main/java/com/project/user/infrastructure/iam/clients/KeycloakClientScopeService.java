package com.project.user.infrastructure.iam.clients;

import com.project.user.domain.iam.clients.ClientScopeService;
import com.project.user.infrastructure.config.KeycloakProperties;
import com.project.user.infrastructure.iam.users.AdminTokenResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakClientScopeService extends AdminTokenResource implements ClientScopeService {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  public KeycloakClientScopeService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.properties = properties;
  }

  @Override
  public boolean checkClientScope(String scopeName) {
    String token = getAdminAccessToken();
    List<Map<String, Object>> scopes =
        webClient
            .get()
            .uri("/admin/realms/" + properties.getRealm() + "/client-scopes")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
            .block();

    return scopes.stream().anyMatch(s -> scopeName.equals(s.get("name")));
  }
}

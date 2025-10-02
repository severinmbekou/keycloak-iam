package com.project.user.infrastructure.iam.clients;

import com.project.user.domain.iam.clients.ClientDeletionService;
import com.project.user.infrastructure.config.KeycloakProperties;
import com.project.user.infrastructure.iam.users.AdminTokenResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class KeycloakClientDeletionService extends AdminTokenResource
    implements ClientDeletionService {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  public KeycloakClientDeletionService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.properties = properties;
  }

  @Override
  public void deleteClient(String id) {
    String token = getAdminAccessToken();
    webClient
        .delete()
        .uri("/admin/realms/" + properties.getRealm() + "/clients/" + id)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .toBodilessEntity()
        .block();
  }
}

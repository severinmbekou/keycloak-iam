package com.project.user.infrastructure.iam.clients;

import com.project.user.application.model.requests.ClientSecret;
import com.project.user.domain.iam.clients.ClientSecretService;
import com.project.user.infrastructure.config.KeycloakProperties;
import com.project.user.infrastructure.iam.users.AdminTokenResource;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class KeycloakClientSecretService extends AdminTokenResource implements ClientSecretService {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  public KeycloakClientSecretService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.properties = properties;
  }

  @Override
  public ClientSecret generateClientSecretKey(String id) {
    String token = getAdminAccessToken();
    Map<String, Object> resp =
        webClient
            .post()
            .uri("/admin/realms/" + properties.getRealm() + "/clients/" + id + "/client-secret")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    return new ClientSecret((String) resp.get("value"));
  }

  @Override
  public ClientSecret getClientSecretKey(String id) {
    String token = getAdminAccessToken();
    Map<String, Object> resp =
        webClient
            .get()
            .uri("/admin/realms/" + properties.getRealm() + "/clients/" + id + "/client-secret")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    return new ClientSecret((String) resp.get("value"));
  }
}

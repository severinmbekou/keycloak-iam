package com.project.user.infrastructure.iam.clients;

import com.project.user.application.model.requests.ClientResponseDto;
import com.project.user.domain.iam.clients.ClientQueryService;
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
public class KeycloakClientQueryService extends AdminTokenResource implements ClientQueryService {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  public KeycloakClientQueryService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.webClient = webClient;
    this.properties = properties;
  }

  @Override
  public ClientResponseDto getClient(String id) {
    String token = getAdminAccessToken();
    Map<String, Object> response =
        webClient
            .get()
            .uri("/admin/realms/" + properties.getRealm() + "/clients/" + id)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    return new ClientResponseDto(
        (String) response.get("id"),
        (String) response.get("clientId"),
        (String) response.get("name"),
        (Boolean) response.get("enabled"));
  }

  @Override
  public List<ClientResponseDto> listClients() {
    String token = getAdminAccessToken();
    List<Map<String, Object>> response =
        webClient
            .get()
            .uri("/admin/realms/" + properties.getRealm() + "/clients")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
            .block();
    log.info("{}", response.getFirst());
    return response.stream()
        .map(
            m ->
                new ClientResponseDto(
                    (String) m.get("id"),
                    (String) m.get("clientId"),
                    (String) m.get("name"),
                    (Boolean) m.get("enabled")))
        .toList();
  }
}

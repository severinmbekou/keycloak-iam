package com.project.user.infrastructure.provider.keycloak;

import com.project.user.infrastructure.config.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AdminTokenResource {
  private final KeycloakProperties properties;
  private final WebClient webClient;

  //TODO Use ReactionAuth and build Webclient auth authenticated
  protected String getAdminAccessToken() {
    return webClient
        .post()
        .uri("/realms/master/protocol/openid-connect/token")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .body(
            BodyInserters.fromFormData("client_id", "admin-cli")
                .with("username", properties.getAdminUsername())
                .with("password", properties.getAdminPassword())
                .with("grant_type", "password"))
        .retrieve()
        .bodyToMono(Map.class)
        .map(m -> (String) m.get("access_token"))
        .block();
  }

  protected String getUserId(String username, String token) {
    return webClient
        .get()
        .uri(
            properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/users?username="
                + username)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .bodyToFlux(Map.class)
        .blockFirst()
        .get("id")
        .toString();
  }
}

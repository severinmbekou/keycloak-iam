package com.project.user.infrastructure.iam.users;

import com.project.user.application.model.LoginRequest;
import com.project.user.domain.iam.users.UserLoginService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserLoginService implements UserLoginService {

  private final KeycloakProperties properties;
  private final WebClient webClient;

  @Override
  public Map<String, Object> login(LoginRequest request) {
    return webClient
        .post()
        .uri("/realms/" + properties.getRealm() + "/protocol/openid-connect/token")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .body(
            BodyInserters.fromFormData("client_id", properties.getClientId())
                .with("client_secret", properties.getClientSecret())
                .with("grant_type", "password")
                .with("username", request.username())
                .with("password", request.password()))
        .retrieve()
        .bodyToMono(Map.class)
        .block();
  }
}

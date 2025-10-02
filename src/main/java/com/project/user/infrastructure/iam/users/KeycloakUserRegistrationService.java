package com.project.user.infrastructure.iam.users;

import com.project.user.application.model.RegisterRequest;
import com.project.user.domain.iam.users.UserRegistrationService;
import com.project.user.infrastructure.config.KeycloakProperties;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakUserRegistrationService extends AdminTokenResource
    implements UserRegistrationService {

  private final WebClient webClient;
  private final String baseUserUrl;

  public KeycloakUserRegistrationService(KeycloakProperties properties, WebClient webClient) {
    super(properties, webClient);
    this.baseUserUrl = "/admin/realms/" + properties.getRealm() + "/users";
    this.webClient = webClient;
  }

  @Override
  public void register(RegisterRequest request) {
    String token = getAdminAccessToken();

    Map<String, Object> user =
        Map.of(
            "username",
            request.username(),
            "email",
            request.email(),
            "emailVerified",
            false,
            "firstName",
            request.firstName(),
            "lastName",
            request.lastName(),
            "enabled",
            request.enabled());

    webClient
        .post()
        .uri(baseUserUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .retrieve()
        .toBodilessEntity()
        .doOnSuccess(res -> log.info("✅ User created successfully : {}", request.username()))
        .doOnError(
            error ->
                log.error("❌ Error creating user: {}, {}", request.username(), error.getMessage()))
        .block();

    List<Map<String, Object>> users =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path(baseUserUrl).queryParam("username", request.username()).build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
            .doOnSuccess(res -> log.info("✅ Retrieved user info: {}", request.username()))
            .doOnError(
                err ->
                    log.error(
                        "❌ Error retrieving user info: {}, {}",
                        request.username(),
                        err.getMessage()))
            .block();

    if (users == null || users.isEmpty()) {
      throw new RuntimeException("User not found after creation");
    }

    String userId = (String) users.getFirst().get("id");

    // Assign password
    Map<String, Object> credentials =
        Map.of("type", "password", "value", request.password(), "temporary", false);

    webClient
        .put()
        .uri(baseUserUrl + "/" + userId + "/reset-password")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(credentials)
        .retrieve()
        .toBodilessEntity()
        .doOnSuccess(res -> log.info("✅ Password set successfully: {}", request.username()))
        .doOnError(
            err ->
                log.error("❌ Error setting password: {}, {}", request.username(), err.getMessage()))
        .block();
  }
}

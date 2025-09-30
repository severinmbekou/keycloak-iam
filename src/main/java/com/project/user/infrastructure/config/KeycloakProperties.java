package com.project.user.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
  private String serverUrl;
  private String realm;
  private String clientId;

  private String clientSecret;
  private String adminUsername;
  private String adminPassword;
}

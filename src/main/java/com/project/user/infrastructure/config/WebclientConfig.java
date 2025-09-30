package com.project.user.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebclientConfig {
  private final KeycloakProperties properties;

  @Bean
  public WebClient webClient() {
    return WebClient.builder().baseUrl(properties.getServerUrl()).build();
  }
}

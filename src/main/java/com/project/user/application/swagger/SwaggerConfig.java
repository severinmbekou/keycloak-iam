package com.project.user.application.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("User Service IAM API")
                .version("1.0")
                .description(
                    "Endpoints for user registration, login, reset password, and client credentials via Keycloak"))
        .addSecurityItem(new SecurityRequirement().addList("oauth2"))
        .components(new Components().addSecuritySchemes("oauth2", securityScheme()));
  }

  private SecurityScheme securityScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.OAUTH2)
        .description("OAuth2 flow with Keycloak")
        .flows(
            new OAuthFlows()
                .authorizationCode(
                    new OAuthFlow()
                        .authorizationUrl(
                            "http://localhost:8080/realms/my-realm/protocol/openid-connect/auth")
                        .tokenUrl(
                            "http://localhost:8080/realms/my-realm/protocol/openid-connect/token")
                        .scopes(
                            new Scopes()
                                .addString("openid", "OpenID Connect scope")
                                .addString("profile", "User profile")
                                .addString("roles", "User roles"))));
  }
}

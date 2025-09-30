package com.project.user.application.rest;

import com.project.user.application.model.LoginRequest;
import com.project.user.application.model.RefreshTokenRequest;
import com.project.user.application.model.RegisterRequest;
import com.project.user.application.model.ResetPasswordRequest;
import com.project.user.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/iam")
public class UserIamController {

  private final UserRegistrationService registrationService;
  private final UserLoginService loginService;
  private final UserPasswordService passwordService;
  private final UserLogoutService logoutService;
  private final UserTokenRefreshService tokenRefreshService;
  private final ClientCredentialService clientCredentialService;

  @Operation(
      summary = "Register a new user",
      description = "Registers a user in Keycloak",
      responses = {@ApiResponse(responseCode = "200", description = "User registered")})
  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
    registrationService.register(request);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "User login",
      description = "Authenticate user with username/password",
      responses = {@ApiResponse(responseCode = "200", description = "Token returned")})
  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(loginService.login(request));
  }

  @Operation(
      summary = "Refresh token",
      description = "Get a new access token using a refresh token",
      responses = {@ApiResponse(responseCode = "200", description = "Token refreshed")})
  @PostMapping("/refresh-token")
  public ResponseEntity<Map<String, Object>> refreshToken(
      @RequestBody RefreshTokenRequest refreshToken) {
    return ResponseEntity.ok(tokenRefreshService.refreshToken(refreshToken.refreshToken()));
  }

  @Operation(
      summary = "Logout",
      description = "Invalidate refresh token and end user session",
      responses = {@ApiResponse(responseCode = "204", description = "User logged out")})
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest refreshToken) {
    logoutService.logout(refreshToken.refreshToken());
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "forgot user password",
      description = "forgot a user’s password",
      responses = {@ApiResponse(responseCode = "200", description = "Password forgot")})
  @PostMapping("/forgot-password")
  public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
    passwordService.resetPassword(request);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get client credentials token",
      description = "Request an access token using client credentials",
      responses = {@ApiResponse(responseCode = "200", description = "Token returned")})
  @PostMapping("/client-credentials")
  public ResponseEntity<Map<String, Object>> clientCredentials() {
    return ResponseEntity.ok(clientCredentialService.clientCredentials());
  }
}

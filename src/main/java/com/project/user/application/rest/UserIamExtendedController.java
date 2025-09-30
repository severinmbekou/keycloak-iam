package com.project.user.application.rest;

import com.project.user.application.model.ChangePasswordRequest;
import com.project.user.application.model.UpdateUserProfileRequest;
import com.project.user.application.model.UserProfileResponse;
import com.project.user.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/iam")
@Tag(name = "Extended IAM", description = "Advanced Identity & Access Management for users")
public class UserIamExtendedController {

  private final UserProfileService profileService;
  private final UserProfileUpdateService profileUpdateService;
  private final UserSelfPasswordService selfPasswordService;
  private final UserEmailService verificationService;
  private final TokenIntrospectionService introspectionService;

  @Operation(
      summary = "Get current user profile",
      description = "Fetch the current user info",
      security = {@SecurityRequirement(name = "oauth2")})
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> me(@RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(profileService.getCurrentUser(token));
  }

  @Operation(
      summary = "Update user profile",
      description = "Update attributes (email, first name, last name)",
      security = {@SecurityRequirement(name = "oauth2")})
  @PutMapping("/{username}/profile")
  public ResponseEntity<Void> updateProfile(
      @PathVariable String username, @RequestBody UpdateUserProfileRequest request) {
    profileUpdateService.updateProfile(username, request);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get user profile",
      description = "Get attributes (email, first name, last name)",
      security = {@SecurityRequirement(name = "oauth2")})
  @GetMapping("/{username}/profile")
  public ResponseEntity<Map<String, Object>> getProfile(@PathVariable String username) {
    return ResponseEntity.ok(profileUpdateService.getProfile(username));
  }

  @Operation(
      summary = "Change password (self-service)",
      description = "User provides old and new password",
      security = {@SecurityRequirement(name = "oauth2")})
  @PostMapping("/{username}/change-password")
  public ResponseEntity<Void> changePassword(
      @PathVariable String username, @RequestBody ChangePasswordRequest request) {
    selfPasswordService.changePassword(username, request);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Introspect token",
      description = "Checks if a token is valid and returns claims")
  @PostMapping("/introspect")
  public ResponseEntity<Map<String, Object>> introspect(@RequestParam String token) {
    return ResponseEntity.ok(introspectionService.introspect(token));
  }

  @Operation(
      summary = "Trigger email verification",
      description = "Sends a verification email to the user",
      security = {@SecurityRequirement(name = "oauth2")})
  @PostMapping("/{username}/verify-email")
  public ResponseEntity<Void> verifyEmail(@PathVariable String username) {
    verificationService.triggerEmailVerification(username);
    return ResponseEntity.ok().build();
  }
}

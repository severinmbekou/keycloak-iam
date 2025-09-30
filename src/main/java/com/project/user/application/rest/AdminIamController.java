package com.project.user.application.rest;

import com.project.user.domain.UserAccountService;
import com.project.user.domain.UserQueryService;
import com.project.user.domain.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/iam")
@Tag(name = "Admin IAM", description = "Administrative Identity & Access Management")
public class AdminIamController {

  private final UserRoleService roleService;
  private final UserAccountService accountService;
  private final UserQueryService userQueryService;

  @Operation(
      summary = "Assign role to user",
      description = "Admin assigns a role to a user",
      security = {@SecurityRequirement(name = "oauth2")})
  @PostMapping("/{username}/roles/{role}")
  public ResponseEntity<Void> assignRole(@PathVariable String username, @PathVariable String role) {
    roleService.assignRole(username, role);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Remove role from user",
      description = "Admin removes a role from a user",
      security = {@SecurityRequirement(name = "oauth2")})
  @DeleteMapping("/{username}/roles/{role}")
  public ResponseEntity<Void> removeRole(@PathVariable String username, @PathVariable String role) {
    roleService.removeRole(username, role);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Disable user account",
      description = "Admin disables a user account",
      security = {@SecurityRequirement(name = "oauth2")})
  @PostMapping("/{username}/disable")
  public ResponseEntity<Void> disable(@PathVariable String username) {
    accountService.disableUser(username);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Disable user account",
      description = "Admin disables a user account",
      security = {@SecurityRequirement(name = "oauth2")})
  @PostMapping("/{username}/enable")
  public ResponseEntity<Void> enable(@PathVariable String username) {
    accountService.enableUser(username);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Delete user account",
      description = "Admin permanently deletes a user account",
      security = {@SecurityRequirement(name = "oauth2")})
  @DeleteMapping("/{username}")
  public ResponseEntity<Void> delete(@PathVariable String username) {
    accountService.deleteUser(username);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{username}")
  public ResponseEntity<Map<String, Object>> getUser(@PathVariable String username) {
    return ResponseEntity.ok(userQueryService.getUserByUsername(username));
  }

  @Operation(
      summary = "List all users",
      description = "Returns a paginated list of users from Keycloak. Requires admin role.",
      security = @SecurityRequirement(name = "oauth2"),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of users retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @ArraySchema(
                            schema =
                                @Schema(
                                    type = "object",
                                    example =
                                        "{ \"id\": \"1234-uuid\", \"username\": \"john\", \"email\": \"john@example.com\", \"enabled\": true }")))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
      })
  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> listAllUsers(
      @Parameter(description = "First result index for pagination", example = "0")
          @RequestParam(defaultValue = "0")
          int first,
      @Parameter(description = "Maximum number of results", example = "20")
          @RequestParam(defaultValue = "20")
          int max) {

    return ResponseEntity.ok(userQueryService.listAllUsers(first, max));
  }
}

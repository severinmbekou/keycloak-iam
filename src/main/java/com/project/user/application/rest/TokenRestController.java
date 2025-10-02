package com.project.user.application.rest;

import com.project.user.application.model.TokenRequest;
import com.project.user.domain.iam.clients.ClientAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iam")
@Tag(name = "Authentication", description = "Authentication endpoints for users and services")
public class TokenRestController {

  private final ClientAuthService clientAuthService;

  @Operation(summary = "Service login with clientId and clientSecret (client_credentials grant)")
  @PostMapping("/token")
  public ResponseEntity<Map<String, Object>> serviceLogin(
      @Valid @RequestBody TokenRequest request) {
    return ResponseEntity.ok(
        clientAuthService.clientLogin(request.clientId(), request.clientSecret()));
  }
}

package com.project.user.application.rest;

import com.project.user.application.model.requests.ClientDto;
import com.project.user.application.model.requests.ClientResponseDto;
import com.project.user.application.model.requests.ClientSecret;
import com.project.user.domain.iam.clients.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iam/clients")
@Tag(name = "Client Management", description = "Manage Keycloak Clients and related IAM features")
@SecurityRequirement(name = "oauth2")
public class ClientRestController {

  private final ClientRegistrationService registrationService;
  private final ClientQueryService queryService;
  private final ClientDeletionService deletionService;
  private final ClientSecretService secretService;
  private final ClientScopeService scopeService;

  // === 1. Create & Update ===
  @Operation(summary = "Create a new client")
  @PostMapping
  public ResponseEntity<Void> createClient(@RequestBody ClientDto dto) {
    registrationService.createClient(dto);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Update an existing client")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateClient(@PathVariable String id, @RequestBody ClientDto dto) {
    registrationService.updateClient(dto, id);
    return ResponseEntity.ok().build();
  }

  // === 2. Query ===
  @Operation(summary = "Get a client by ID")
  @GetMapping("/{id}")
  public ResponseEntity<ClientResponseDto> getClient(@PathVariable String id) {
    return ResponseEntity.ok(queryService.getClient(id));
  }

  @Operation(summary = "List all clients")
  @GetMapping
  public ResponseEntity<List<ClientResponseDto>> listClients() {
    return ResponseEntity.ok(queryService.listClients());
  }

  // === 3. Delete ===
  @Operation(summary = "Delete a client")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClient(@PathVariable String id) {
    deletionService.deleteClient(id);
    return ResponseEntity.noContent().build();
  }

  // === 4. Secrets ===
  @Operation(summary = "Generate a new client secret")
  @PostMapping("/{id}/secret")
  public ResponseEntity<ClientSecret> generateSecret(@PathVariable String id) {
    return ResponseEntity.ok(secretService.generateClientSecretKey(id));
  }

  @Operation(summary = "Get the current client secret")
  @GetMapping("/{id}/secret")
  public ResponseEntity<ClientSecret> getSecret(@PathVariable String id) {
    return ResponseEntity.ok(secretService.getClientSecretKey(id));
  }

  // === 5. Scope Validation ===
  @Operation(summary = "Check if a client scope exists")
  @GetMapping("/scopes/{scopeName}/check")
  public ResponseEntity<Boolean> checkScope(@PathVariable String scopeName) {
    return ResponseEntity.ok(scopeService.checkClientScope(scopeName));
  }
}

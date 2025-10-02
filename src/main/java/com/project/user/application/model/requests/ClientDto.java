package com.project.user.application.model.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ClientDto(
    String id,
    String clientId,
    String secret,
    String name,
    String protocol,
    String clientAuthenticatorType,
    Boolean publicClient,
    Boolean serviceAccountsEnabled,
    Boolean standardFlowEnabled,
    Map<String, String> attributes,
    Boolean enabled) {
  public ClientDto {
    protocol = "openid-connect";
    publicClient = Boolean.FALSE;
    serviceAccountsEnabled = Boolean.TRUE;
    standardFlowEnabled = Boolean.FALSE;
    clientAuthenticatorType = "client-secret";
    attributes = Map.of("owner", UUID.randomUUID().toString());
  }
}

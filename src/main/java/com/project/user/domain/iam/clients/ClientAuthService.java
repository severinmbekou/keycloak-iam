package com.project.user.domain.iam.clients;

import java.util.Map;

public interface ClientAuthService {
    Map<String, Object> clientLogin(String clientId, String clientSecret);
}
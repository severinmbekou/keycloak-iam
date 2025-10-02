package com.project.user.domain.iam.clients;

import com.project.user.application.model.requests.ClientSecret;

public interface ClientSecretService {
    ClientSecret generateClientSecretKey(String id);
    ClientSecret getClientSecretKey(String id);
}

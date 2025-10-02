package com.project.user.infrastructure.iam.clients;

import com.project.user.application.model.requests.ClientDto;
import com.project.user.domain.iam.clients.ClientRegistrationService;
import com.project.user.infrastructure.config.KeycloakProperties;
import com.project.user.infrastructure.iam.users.AdminTokenResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
@Service
@Slf4j
public class KeycloakClientRegistrationService extends AdminTokenResource implements ClientRegistrationService {
    private final KeycloakProperties properties;
    private final WebClient webClient;   public KeycloakClientRegistrationService(KeycloakProperties properties, WebClient webClient) {
        super(properties, webClient);
        this.webClient = webClient;
        this.properties = properties;
    }

    @Override
    public void createClient(ClientDto dto) {
        String token = getAdminAccessToken();
        webClient.post()
                .uri("/admin/realms/" + properties.getRealm() + "/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void updateClient(ClientDto dto, String id) {
        String token = getAdminAccessToken();
        webClient.put()
                .uri("/admin/realms/" + properties.getRealm() + "/clients/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

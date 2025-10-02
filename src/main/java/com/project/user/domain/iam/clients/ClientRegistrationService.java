package com.project.user.domain.iam.clients;

import com.project.user.application.model.requests.ClientDto;

public interface ClientRegistrationService {
  void createClient(ClientDto dto);

  void updateClient(ClientDto dto, String id);
}

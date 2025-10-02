package com.project.user.domain.iam.clients;

import com.project.user.application.model.requests.ClientResponseDto;

import java.util.List;

public interface ClientQueryService {
  ClientResponseDto getClient(String id);

  List<ClientResponseDto> listClients();
}

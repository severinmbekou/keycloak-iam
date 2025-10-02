package com.project.user.application.model.requests;

import java.util.List;

public record ClientResponseDto(
        String id,
        String clientId,
        String name,
        boolean enabled
) {}


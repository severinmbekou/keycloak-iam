package com.project.user.domain;

import com.project.user.application.model.RegisterRequest;

public interface UserRegistrationService {
    void register(RegisterRequest request);
}


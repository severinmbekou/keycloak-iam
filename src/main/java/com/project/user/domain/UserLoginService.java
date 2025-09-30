package com.project.user.domain;

import com.project.user.application.model.LoginRequest;

import java.util.Map;

public interface UserLoginService {
    Map<String, Object> login(LoginRequest request);
}

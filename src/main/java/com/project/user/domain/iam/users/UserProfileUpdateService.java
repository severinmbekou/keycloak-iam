package com.project.user.domain.iam.users;

import com.project.user.application.model.UpdateUserProfileRequest;

import java.util.Map;

public interface UserProfileUpdateService {
    void updateProfile(String username, UpdateUserProfileRequest request);

    Map<String, Object> getProfile(String username);
}

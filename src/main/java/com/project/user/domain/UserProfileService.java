package com.project.user.domain;

import com.project.user.application.model.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getCurrentUser(String accessToken);
}

package com.project.user.domain.iam.users;

import com.project.user.application.model.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getCurrentUser(String accessToken);
}

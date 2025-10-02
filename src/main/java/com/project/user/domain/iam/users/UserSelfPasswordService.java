package com.project.user.domain.iam.users;

import com.project.user.application.model.ChangePasswordRequest;

public interface UserSelfPasswordService {
    void changePassword(String username, ChangePasswordRequest request);
}

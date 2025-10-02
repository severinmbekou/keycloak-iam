package com.project.user.domain.iam.users;

import com.project.user.application.model.ResetPasswordRequest;

public interface UserPasswordService {
    void resetPassword(ResetPasswordRequest request);
}

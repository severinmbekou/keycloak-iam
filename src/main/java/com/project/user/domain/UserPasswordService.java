package com.project.user.domain;

import com.project.user.application.model.ResetPasswordRequest;

public interface UserPasswordService {
    void resetPassword(ResetPasswordRequest request);
}

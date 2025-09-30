package com.project.user.domain;

import com.project.user.application.model.ResetPasswordRequest;

public interface UserLogoutService {
  void logout(String refreshToken);
}

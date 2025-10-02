package com.project.user.domain.iam.users;

public interface UserLogoutService {
  void logout(String refreshToken);
}

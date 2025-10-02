package com.project.user.domain.iam.users;

public interface UserEmailService  {
  void triggerEmailVerification(String username);
    void sendForgotPasswordEmail(String username);
}

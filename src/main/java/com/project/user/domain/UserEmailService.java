package com.project.user.domain;

public interface UserEmailService  {
  void triggerEmailVerification(String username);
    void sendForgotPasswordEmail(String username);
}

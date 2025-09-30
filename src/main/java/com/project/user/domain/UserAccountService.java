package com.project.user.domain;

public interface UserAccountService {
  void disableUser(String username);

  void enableUser(String username);

  void deleteUser(String username);
}

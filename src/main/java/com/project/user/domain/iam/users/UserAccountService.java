package com.project.user.domain.iam.users;

public interface UserAccountService {
  void disableUser(String username);

  void enableUser(String username);

  void deleteUser(String username);
}

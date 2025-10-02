package com.project.user.domain.iam.users;

public interface UserRoleService {
    void assignRole(String username, String roleName);
    void removeRole(String username, String roleName);
}

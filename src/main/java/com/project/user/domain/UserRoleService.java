package com.project.user.domain;

public interface UserRoleService {
    void assignRole(String username, String roleName);
    void removeRole(String username, String roleName);
}

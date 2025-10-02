package com.project.user.domain.iam.users;

import java.util.List;
import java.util.Map;

public interface UserQueryService {
    List<Map<String, Object>> listAllUsers(int first, int max);
    Map<String, Object> getUserByUsername(String username);
}

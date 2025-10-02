package com.project.user.domain.iam.users;

import java.util.Map;

public interface UserTokenRefreshService {
  Map<String, Object> refreshToken(String refreshToken);
}

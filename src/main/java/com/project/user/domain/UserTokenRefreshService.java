package com.project.user.domain;

import java.util.Map;

public interface UserTokenRefreshService {
  Map<String, Object> refreshToken(String refreshToken);
}

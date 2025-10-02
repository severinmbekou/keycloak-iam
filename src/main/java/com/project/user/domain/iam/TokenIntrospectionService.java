package com.project.user.domain.iam;

import java.util.Map;

public interface TokenIntrospectionService {
  Map<String, Object> introspect(String token);
}

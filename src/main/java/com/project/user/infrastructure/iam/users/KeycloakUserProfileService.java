package com.project.user.infrastructure.iam.users;

import com.project.user.application.model.UserProfileResponse;
import com.project.user.domain.iam.users.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserProfileService implements UserProfileService {
  @Override
  public UserProfileResponse getCurrentUser(String accessToken) {
    return UserProfileResponse.builder().name("testName").email("demo@gmail.com").build();
  }
}

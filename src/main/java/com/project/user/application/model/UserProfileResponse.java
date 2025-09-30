package com.project.user.application.model;

import lombok.Builder;

@Builder
public record UserProfileResponse(String name, String email) {}

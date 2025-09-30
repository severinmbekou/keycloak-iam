package com.project.user.application.model;

public record UpdateUserProfileRequest(String firstName, String lastName, boolean emailVerified) {}

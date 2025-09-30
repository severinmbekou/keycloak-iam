package com.project.user.application.model;

public record RegisterRequest(
    String username,
    String email,
    String password,
    boolean emailVerified,
    String firstName,
    String lastName,
    boolean enabled) {}

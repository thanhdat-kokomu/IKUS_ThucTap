package com.tu00128.taskmgmt.dto.response;

import java.util.Set;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;  // THÊM MỚI

    // Constructor cũ
    public UserResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Constructor mới với roles
    public UserResponse(Long id, String username, String email, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {  // THÊM MỚI
        return roles;
    }
}
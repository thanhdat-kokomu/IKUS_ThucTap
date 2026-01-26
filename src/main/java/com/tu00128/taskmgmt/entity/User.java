package com.tu00128.taskmgmt.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private LocalDateTime createdAt;

    // Regex đơn giản cho email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Constructor chính
    public User(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = true;
        this.createdAt = LocalDateTime.now();

        validate();
    }

    // Validate nghiệp vụ User
    private void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được rỗng");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được rỗng");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không đúng định dạng");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password phải >= 6 ký tự");
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Hành vi nghiệp vụ đơn giản
    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    // equals & hashCode (so sánh theo id)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


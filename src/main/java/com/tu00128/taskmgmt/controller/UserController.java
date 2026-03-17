package com.tu00128.taskmgmt.controller;

import com.tu00128.taskmgmt.dto.request.CreateUserRequest;
import com.tu00128.taskmgmt.dto.response.ApiResponse;
import com.tu00128.taskmgmt.dto.response.UserResponse;
import com.tu00128.taskmgmt.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user with username: {}, email: {}", request.getUsername(), request.getEmail());

        try {
            UserResponse user = userService.createUser(request);
            log.info("User created successfully with ID: {}", user.getId());
            return ApiResponse.success("User created successfully", user);
        } catch (Exception e) {
            log.error("Error creating user:", e);
            throw e;
        }
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponse> users = userService.getAllUsers();
        log.info("Found {} users", users.size());
        return ApiResponse.success(users);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
}
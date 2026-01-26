package com.tu00128.taskmgmt.controller;

import com.tu00128.taskmgmt.dto.UserResponse;
import com.tu00128.taskmgmt.entity.UserEntity;
import com.tu00128.taskmgmt.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Create user
    @PostMapping
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<UserResponse> getUsers1() {
        return userService.getAllUsers1();
    }
    // 3. Get user by id
    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}

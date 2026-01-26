package com.tu00128.taskmgmt.service;

import com.tu00128.taskmgmt.dto.UserResponse;
import com.tu00128.taskmgmt.entity.UserEntity;
import com.tu00128.taskmgmt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers1() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
                .toList();
    }


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Tạo user mới
    public UserEntity createUser(UserEntity user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        return userRepository.save(user);
    }

    // Lấy tất cả user
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo id
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

package com.tu00128.taskmgmt.service;

import com.tu00128.taskmgmt.dto.request.CreateUserRequest;
import com.tu00128.taskmgmt.dto.request.RegisterRequest;
import com.tu00128.taskmgmt.dto.response.UserResponse;
import com.tu00128.taskmgmt.entity.RoleEntity;
import com.tu00128.taskmgmt.entity.UserEntity;
import com.tu00128.taskmgmt.exception.BusinessException;
import com.tu00128.taskmgmt.exception.ErrorCode;
import com.tu00128.taskmgmt.repository.RoleRepository;
import com.tu00128.taskmgmt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;  // THÊM

    // Cập nhật constructor
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {  // THÊM
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;  // THÊM
    }

    // THÊM METHOD REGISTER MỚI
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Starting registration for: {}", request.getUsername());

        // Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                    "Username already exists: " + request.getUsername());
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                    "Email already exists: " + request.getEmail());
        }

        // Hash password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("Password encoded successfully");

        // Tạo user mới
        UserEntity user = new UserEntity(
                request.getUsername(),
                request.getEmail(),
                encodedPassword
        );

        // Gán role USER mặc định
        RoleEntity userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "Default role not found"));
        user.addRole(userRole);

        // Save
        UserEntity savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return mapToUserResponse(savedUser);
    }

    // SỬA METHOD CREATEUSER HIỆN TẠI
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Starting createUser process for: {}", request.getUsername());

        try {
            // Check username uniqueness
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                        "Username already exists: " + request.getUsername());
            }

            // Check email uniqueness
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                        "Email already exists: " + request.getEmail());
            }

            // Hash password
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            log.debug("Password encoded successfully");

            // Tạo entity từ request
            UserEntity user = new UserEntity(
                    request.getUsername(),
                    request.getEmail(),
                    encodedPassword  // Dùng password đã hash
            );

            // Gán role USER mặc định
            RoleEntity userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "Default role not found"));
            user.addRole(userRole);

            // Save to database
            UserEntity savedUser = userRepository.save(user);
            log.info("User saved successfully with ID: {}", savedUser.getId());

            return mapToUserResponse(savedUser);

        } catch (BusinessException e) {
            log.warn("Business exception in createUser: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in createUser:", e);
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    // Các method khác giữ nguyên
    @Transactional
    public UserResponse addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user {}", roleName, userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "Role not found: " + roleName));

        user.addRole(role);
        UserEntity savedUser = userRepository.save(user);

        log.info("Role {} added to user {}", roleName, userId);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse removeRoleFromUser(Long userId, String roleName) {
        log.info("Removing role {} from user {}", roleName, userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "Role not found: " + roleName));

        user.removeRole(role);
        UserEntity savedUser = userRepository.save(user);

        log.info("Role {} removed from user {}", roleName, userId);
        return mapToUserResponse(savedUser);
    }

    public Set<String> getUserRoles(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }

    private UserResponse mapToUserResponse(UserEntity user) {
        Set<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users from database");
        try {
            List<UserResponse> users = userRepository.findAll()
                    .stream()
                    .map(this::mapToUserResponse)
                    .toList();
            log.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error fetching all users:", e);
            throw e;
        }
    }

    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        try {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", id);
                        return new BusinessException(ErrorCode.USER_NOT_FOUND);
                    });

            return mapToUserResponse(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by ID {}:", id, e);
            throw e;
        }
    }
}
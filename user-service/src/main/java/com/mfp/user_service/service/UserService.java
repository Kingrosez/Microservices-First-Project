package com.mfp.user_service.service;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.entity.UserEntity;
import com.mfp.user_service.enums.Role;
import com.mfp.user_service.exception.*;
import com.mfp.user_service.mapper.UserMapper;
import com.mfp.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ============ CREATE ============

    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        log.info("Creating new user with email: {}", createUserRequest.email());
        
        // Check for duplicate email
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new DuplicateEmailException(createUserRequest.email());
        }
        
        // Validate role
        validateRole(createUserRequest.role());
        
        UserEntity userEntity = userMapper.toEntity(createUserRequest);
        userEntity.setPassword(passwordEncoder.encode(createUserRequest.password()));
        UserEntity saved = userRepository.save(userEntity);
        
        log.info("User created successfully with id: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    // ============ READ ============

    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        Page<UserEntity> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    public List<UserResponse> getAllActiveUsers() {
        log.info("Fetching all active users");
        List<UserEntity> users = userRepository.findByActiveTrueOrderByIdDesc();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllInactiveUsers() {
        log.info("Fetching all inactive users");
        List<UserEntity> users = userRepository.findByActiveFalseOrderByIdDesc();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(String role) {
        log.info("Fetching users by role: {}", role);
        validateRole(role);
        Role roleEnum = Role.valueOf(role);
        List<UserEntity> users = userRepository.findByRoleOrderByNameAsc(roleEnum);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public boolean userExistsByEmail(String email) {
        log.info("Checking if user exists with email: {}", email);
        return userRepository.existsByEmail(email);
    }

    public long getTotalUserCount() {
        log.info("Fetching total user count");
        return userRepository.count();
    }

    // ============ UPDATE ============

    @Transactional
    public UserResponse updateUserName(Long id, String name) {
        log.info("Updating user name for id: {}", id);
        UserEntity user = getUserEntityById(id);
        user.setName(name);
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public UserResponse updateUserEmail(Long id, String email) {
        log.info("Updating user email for id: {}", id);
        
        // Check for duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        
        UserEntity user = getUserEntityById(id);
        user.setEmail(email);
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, String role) {
        log.info("Updating user role for id: {}", id);
        validateRole(role);
        
        UserEntity user = getUserEntityById(id);
        user.setRole(Role.valueOf(role));
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public void updateUserPassword(Long id, String newPassword) {
        log.info("Updating user password for id: {}", id);
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new InvalidPasswordException("Password cannot be empty");
        }
        
        UserEntity user = getUserEntityById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public UserResponse activateUser(Long id) {
        log.info("Activating user with id: {}", id);
        UserEntity user = getUserEntityById(id);
        user.setActive(true);
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);
        UserEntity user = getUserEntityById(id);
        user.setActive(false);
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    // ============ DELETE ============

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        log.info("Deleting user with email: {}", email);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        userRepository.delete(user);
    }

    @Transactional
    public long deleteAllInactiveUsers() {
        log.info("Deleting all inactive users");
        List<UserEntity> inactiveUsers = userRepository.findByActiveFalse();
        long count = inactiveUsers.size();
        userRepository.deleteAll(inactiveUsers);
        return count;
    }

    // ============ SEARCH & FILTER ============

    public List<UserResponse> searchUsersByName(String name) {
        log.info("Searching users by name: {}", name);
        List<UserEntity> users = userRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public long getUserCountByRole(String role) {
        log.info("Getting user count by role: {}", role);
        validateRole(role);
        Role roleEnum = Role.valueOf(role);
        return userRepository.countByRole(roleEnum);
    }

    public long getActiveUserCount() {
        log.info("Getting active user count");
        return userRepository.countByActiveTrue();
    }

    public long getInactiveUserCount() {
        log.info("Getting inactive user count");
        return userRepository.countByActiveFalse();
    }

    // ============ BULK OPERATIONS ============

    @Transactional
    public long activateMultipleUsers(List<Long> userIds) {
        log.info("Activating multiple users: {}", userIds);
        List<UserEntity> users = userRepository.findAllById(userIds);
        users.forEach(user -> user.setActive(true));
        userRepository.saveAll(users);
        return users.size();
    }

    @Transactional
    public long deactivateMultipleUsers(List<Long> userIds) {
        log.info("Deactivating multiple users: {}", userIds);
        List<UserEntity> users = userRepository.findAllById(userIds);
        users.forEach(user -> user.setActive(false));
        userRepository.saveAll(users);
        return users.size();
    }

    @Transactional
    public long deleteMultipleUsers(List<Long> userIds) {
        log.info("Deleting multiple users: {}", userIds);
        List<UserEntity> users = userRepository.findAllById(userIds);
        long count = users.size();
        userRepository.deleteAll(users);
        return count;
    }

    // ============ HELPER METHODS ============

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validateRole(String role) {
        try {
            Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException(role);
        }
    }
}

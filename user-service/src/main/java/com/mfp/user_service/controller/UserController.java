package com.mfp.user_service.controller;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/v1")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // ============ CREATE ============

    /**
     * Create a new user
     * 
     * @param createUserRequest User creation request
     * @return Created user response with 201 status
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Request to create user: {}", createUserRequest);
        UserResponse user = userService.createUser(createUserRequest);
        log.info("User creation was successful with id: {}", user.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // ============ READ ============

    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User details with 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Request to get user by id: {}", id);
        UserResponse user = userService.getUserById(id);
        log.info("User retrieved successfully with id: {}", id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by email
     * 
     * @param email User email
     * @return User details with 200 status
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Request to get user by email: {}", email);
        UserResponse user = userService.getUserByEmail(email);
        log.info("User retrieved successfully with email: {}", email);
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users with pagination and sorting
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Sort field (default: id)
     * @param sortOrder Sort order (default: ASC)
     * @return Paginated list of users
     */
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder) {
        log.info("Request to get all users with pagination - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                page, size, sortBy, sortOrder);
        
        Sort.Direction direction = Sort.Direction.valueOf(sortOrder.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UserResponse> users = userService.getAllUsers(pageable);
        log.info("Retrieved {} users from page {}", users.getNumberOfElements(), page);
        return ResponseEntity.ok(users);
    }

    /**
     * Get all active users
     * 
     * @return List of active users
     */
    @GetMapping("/active/all")
    public ResponseEntity<List<UserResponse>> getAllActiveUsers() {
        log.info("Request to get all active users");
        List<UserResponse> users = userService.getAllActiveUsers();
        log.info("Retrieved {} active users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Get all inactive users
     * 
     * @return List of inactive users
     */
    @GetMapping("/inactive/all")
    public ResponseEntity<List<UserResponse>> getAllInactiveUsers() {
        log.info("Request to get all inactive users");
        List<UserResponse> users = userService.getAllInactiveUsers();
        log.info("Retrieved {} inactive users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Get all users by role
     * 
     * @param role User role (USER or ADMIN)
     * @return List of users with specified role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        log.info("Request to get users by role: {}", role);
        List<UserResponse> users = userService.getUsersByRole(role);
        log.info("Retrieved {} users with role: {}", users.size(), role);
        return ResponseEntity.ok(users);
    }

    /**
     * Check if user exists by email
     * 
     * @param email User email
     * @return true if user exists, false otherwise
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> userExistsByEmail(@PathVariable String email) {
        log.info("Request to check if user exists by email: {}", email);
        boolean exists = userService.userExistsByEmail(email);
        log.info("User exists check for email {}: {}", email, exists);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get total count of users
     * 
     * @return Total number of users
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalUserCount() {
        log.info("Request to get total user count");
        long count = userService.getTotalUserCount();
        log.info("Total user count: {}", count);
        return ResponseEntity.ok(count);
    }

    // ============ UPDATE ============

    /**
     * Update user name
     * 
     * @param id User ID
     * @param name New user name
     * @return Updated user response
     */
    @PutMapping("/{id}/name")
    public ResponseEntity<UserResponse> updateUserName(
            @PathVariable Long id,
            @RequestParam String name) {
        log.info("Request to update user name for id: {} with name: {}", id, name);
        UserResponse user = userService.updateUserName(id, name);
        log.info("User name updated successfully for id: {}", id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user email
     * 
     * @param id User ID
     * @param email New email
     * @return Updated user response
     */
    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateUserEmail(
            @PathVariable Long id,
            @RequestParam String email) {
        log.info("Request to update user email for id: {} with email: {}", id, email);
        UserResponse user = userService.updateUserEmail(id, email);
        log.info("User email updated successfully for id: {}", id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user role
     * 
     * @param id User ID
     * @param role New role
     * @return Updated user response
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        log.info("Request to update user role for id: {} with role: {}", id, role);
        UserResponse user = userService.updateUserRole(id, role);
        log.info("User role updated successfully for id: {}", id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user password
     * 
     * @param id User ID
     * @param newPassword New password
     * @return Success message with 200 status
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<String> updateUserPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        log.info("Request to update user password for id: {}", id);
        userService.updateUserPassword(id, newPassword);
        log.info("User password updated successfully for id: {}", id);
        return ResponseEntity.ok("Password updated successfully");
    }

    /**
     * Activate user
     * 
     * @param id User ID
     * @return Updated user response
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        log.info("Request to activate user with id: {}", id);
        UserResponse user = userService.activateUser(id);
        log.info("User activated successfully with id: {}", id);
        return ResponseEntity.ok(user);
    }

    /**
     * Deactivate user
     * 
     * @param id User ID
     * @return Updated user response
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        log.info("Request to deactivate user with id: {}", id);
        UserResponse user = userService.deactivateUser(id);
        log.info("User deactivated successfully with id: {}", id);
        return ResponseEntity.ok(user);
    }

    // ============ DELETE ============

    /**
     * Delete user by ID
     * 
     * @param id User ID
     * @return Success message with 200 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        log.info("Request to delete user with id: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully with id: {}", id);
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * Delete user by email
     * 
     * @param email User email
     * @return Success message with 200 status
     */
    @DeleteMapping("/email/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        log.info("Request to delete user with email: {}", email);
        userService.deleteUserByEmail(email);
        log.info("User deleted successfully with email: {}", email);
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * Delete all inactive users
     * 
     * @return Count of deleted users
     */
    @DeleteMapping("/inactive/all")
    public ResponseEntity<Long> deleteAllInactiveUsers() {
        log.info("Request to delete all inactive users");
        long deletedCount = userService.deleteAllInactiveUsers();
        log.info("Deleted {} inactive users", deletedCount);
        return ResponseEntity.ok(deletedCount);
    }

    // ============ SEARCH & FILTER ============

    /**
     * Search users by name (partial match)
     * 
     * @param name User name or partial name
     * @return List of matching users
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<UserResponse>> searchUsersByName(@RequestParam String name) {
        log.info("Request to search users by name: {}", name);
        List<UserResponse> users = userService.searchUsersByName(name);
        log.info("Found {} users matching name: {}", users.size(), name);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user count by role
     * 
     * @param role User role
     * @return Count of users with specified role
     */
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> getUserCountByRole(@PathVariable String role) {
        log.info("Request to get user count by role: {}", role);
        long count = userService.getUserCountByRole(role);
        log.info("User count for role {}: {}", role, count);
        return ResponseEntity.ok(count);
    }

    /**
     * Get active user count
     * 
     * @return Count of active users
     */
    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveUserCount() {
        log.info("Request to get active user count");
        long count = userService.getActiveUserCount();
        log.info("Active user count: {}", count);
        return ResponseEntity.ok(count);
    }

    /**
     * Get inactive user count
     * 
     * @return Count of inactive users
     */
    @GetMapping("/count/inactive")
    public ResponseEntity<Long> getInactiveUserCount() {
        log.info("Request to get inactive user count");
        long count = userService.getInactiveUserCount();
        log.info("Inactive user count: {}", count);
        return ResponseEntity.ok(count);
    }

    // ============ BULK OPERATIONS ============

    /**
     * Activate multiple users
     * 
     * @param userIds List of user IDs
     * @return Count of activated users
     */
    @PutMapping("/bulk/activate")
    public ResponseEntity<Long> activateMultipleUsers(@RequestBody List<Long> userIds) {
        log.info("Request to activate multiple users: {}", userIds);
        long activatedCount = userService.activateMultipleUsers(userIds);
        log.info("Activated {} users", activatedCount);
        return ResponseEntity.ok(activatedCount);
    }

    /**
     * Deactivate multiple users
     * 
     * @param userIds List of user IDs
     * @return Count of deactivated users
     */
    @PutMapping("/bulk/deactivate")
    public ResponseEntity<Long> deactivateMultipleUsers(@RequestBody List<Long> userIds) {
        log.info("Request to deactivate multiple users: {}", userIds);
        long deactivatedCount = userService.deactivateMultipleUsers(userIds);
        log.info("Deactivated {} users", deactivatedCount);
        return ResponseEntity.ok(deactivatedCount);
    }

    /**
     * Delete multiple users
     * 
     * @param userIds List of user IDs
     * @return Count of deleted users
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<Long> deleteMultipleUsers(@RequestBody List<Long> userIds) {
        log.info("Request to delete multiple users: {}", userIds);
        long deletedCount = userService.deleteMultipleUsers(userIds);
        log.info("Deleted {} users", deletedCount);
        return ResponseEntity.ok(deletedCount);
    }

    // ============ HEALTH CHECK ============

    /**
     * Health check endpoint
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("Health check request");
        return ResponseEntity.ok("User Service is healthy");
    }
}

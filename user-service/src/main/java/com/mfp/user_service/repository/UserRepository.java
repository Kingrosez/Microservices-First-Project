package com.mfp.user_service.repository;

import com.mfp.user_service.entity.UserEntity;
import com.mfp.user_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // ============ FIND OPERATIONS ============
    
    /**
     * Find user by email
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all active users ordered by id descending
     */
    List<UserEntity> findByActiveTrueOrderByIdDesc();
    
    /**
     * Find all inactive users ordered by id descending
     */
    List<UserEntity> findByActiveFalseOrderByIdDesc();
    
    /**
     * Find all inactive users
     */
    List<UserEntity> findByActiveFalse();
    
    /**
     * Find users by role ordered by name ascending
     */
    List<UserEntity> findByRoleOrderByNameAsc(Role role);
    
    /**
     * Find users by name containing (case-insensitive) ordered by name
     */
    List<UserEntity> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    
    // ============ COUNT OPERATIONS ============
    
    /**
     * Count users by role
     */
    long countByRole(Role role);
    
    /**
     * Count active users
     */
    long countByActiveTrue();
    
    /**
     * Count inactive users
     */
    long countByActiveFalse();
}

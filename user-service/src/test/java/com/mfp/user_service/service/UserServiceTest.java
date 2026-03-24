package com.mfp.user_service.service;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.entity.UserEntity;
import com.mfp.user_service.enums.Role;
import com.mfp.user_service.mapper.UserMapper;
import com.mfp.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "password123", "USER");
        UserEntity userEntity = UserEntity.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .build();
        UserEntity savedEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .build();
        UserResponse expectedResponse = new UserResponse(1L, "John Doe", "john@example.com", "USER", true);

        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("John Doe", response.name());
        assertEquals("john@example.com", response.email());
        assertEquals("USER", response.role());
        assertTrue(response.active());

        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(userEntity);
        verify(userMapper).toResponse(savedEntity);
    }

    @Test
    void createUser_shouldThrowExceptionForInvalidRole() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "password123", "INVALID_ROLE");

        when(userMapper.toEntity(request)).thenThrow(new IllegalArgumentException("Invalid role"));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertEquals("Invalid role", exception.getMessage());

        verify(userMapper).toEntity(request);
        verifyNoInteractions(passwordEncoder, userRepository);
    }

    @Test
    void createUser_shouldHandleRepositorySaveFailure() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "password123", "USER");
        UserEntity userEntity = UserEntity.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .build();

        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(request));
        assertEquals("Database error", exception.getMessage());

        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void createUser_shouldThrowExceptionForDuplicateEmail() {
        // Given
        CreateUserRequest request = new CreateUserRequest("Jane Doe", "john@example.com", "password123", "USER");
        UserEntity userEntity = UserEntity.builder()
                .name("Jane Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .build();

        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate email"));

        // When & Then
        org.springframework.dao.DataIntegrityViolationException exception = assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class, () -> userService.createUser(request));
        assertEquals("Duplicate email", exception.getMessage());

        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void createUser_shouldSetDefaultValues() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "password123", "ADMIN");
        UserEntity userEntity = UserEntity.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .active(true)  // Default value
                .build();
        UserEntity savedEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .active(true)
                .build();
        UserResponse expectedResponse = new UserResponse(1L, "John Doe", "john@example.com", "ADMIN", true);

        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertNotNull(response);
        assertTrue(response.active());  // Verify default active is true

        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(userEntity);
        verify(userMapper).toResponse(savedEntity);
    }

    @Test
    void createUser_shouldHandlePasswordEncoding() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "plainPassword", "USER");
        UserEntity userEntity = UserEntity.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .active(true)
                .build();
        UserEntity savedEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .active(true)
                .build();
        UserResponse expectedResponse = new UserResponse(1L, "John Doe", "john@example.com", "USER", true);

        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertNotNull(response);
        // Verify that password is encoded and set on the entity
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(argThat(entity -> "hashedPassword".equals(entity.getPassword())));
    }

    @Test
    void createUser_shouldThrowExceptionForNullRequest() {
        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> userService.createUser(null));
        // Since the method calls userMapper.toEntity(null), which may throw NPE

        verify(userMapper).toEntity(null);
        verifyNoInteractions(passwordEncoder, userRepository);
    }
}

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
import static org.mockito.ArgumentMatchers.any;
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
}

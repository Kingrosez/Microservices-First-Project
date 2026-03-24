package com.mfp.user_service.service;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.entity.UserEntity;
import com.mfp.user_service.mapper.UserMapper;
import com.mfp.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        log.info("Received request to create user");
        UserEntity userEntity = userMapper.toEntity(createUserRequest);
        UserEntity saved = userRepository.save(userEntity);
        log.info("User created successfully");
        return userMapper.toResponse(saved);
    }
}

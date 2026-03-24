package com.mfp.user_service.controller;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/v1")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService  userService;
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Request to create user : {}", createUserRequest);
        UserResponse user = userService.createUser(createUserRequest);
        log.info("User creation was successful with id : {}", user.id());
        return ResponseEntity.ok().body(user);

    }
}

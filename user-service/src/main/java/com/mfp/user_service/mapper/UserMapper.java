package com.mfp.user_service.mapper;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    UserEntity toEntity(CreateUserRequest createUserRequest);

    @Mapping(target = "role", expression = "java(userEntity.getRole().name())")
    UserResponse toResponse(UserEntity userEntity);
}
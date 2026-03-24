package com.mfp.user_service.mapper;

import com.mfp.user_service.dtos.CreateUserRequest;
import com.mfp.user_service.dtos.UserResponse;
import com.mfp.user_service.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    UserEntity toEntity(CreateUserRequest createUserRequest);

    @Mapping(target = "role", expression = "java(userEntity.getRole().name())")
    UserResponse toResponse(UserEntity userEntity);

    List<UserResponse> toUserResponseList(List<UserEntity> userEntityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CreateUserRequest request, @MappingTarget UserEntity userEntity);
}
package com.MFP.OrderService.mapper;

import com.MFP.OrderService.dto.CreateOrderRequest;
import com.MFP.OrderService.dto.OrderResponse;
import com.MFP.OrderService.dto.UpdateOrderRequestDto;
import com.MFP.OrderService.entity.OrderEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderEntity toEntity(CreateOrderRequest createOrderRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateOrderRequestDto  updateOrderRequestDto, @MappingTarget OrderEntity orderEntity );


    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    OrderResponse toResponse(OrderEntity orderEntity);
}

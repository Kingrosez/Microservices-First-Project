package com.MFP.OrderService.service;

import com.MFP.OrderService.dto.CreateOrderRequest;
import com.MFP.OrderService.dto.OrderResponse;
import com.MFP.OrderService.dto.UpdateOrderRequestDto;
import com.MFP.OrderService.entity.OrderEntity;
import com.MFP.OrderService.enums.OrderStatus;
import com.MFP.OrderService.exception.InvalidOrderException;
import com.MFP.OrderService.exception.OrderAlreadyExistsException;
import com.MFP.OrderService.exception.OrderNotFoundException;
import com.MFP.OrderService.mapper.OrderMapper;
import com.MFP.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    public OrderResponse createOrder(CreateOrderRequest createOrderRequest){
        if(createOrderRequest.getUserId() == null){
            throw new InvalidOrderException("User id must not be null");
        }
        if(createOrderRequest.getAmount()==null || createOrderRequest.getAmount().compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidOrderException("Amount must be greater than zero");
        }
        log.info("Create order request was received for user id={}, and amount={} ",createOrderRequest.getUserId(), createOrderRequest.getAmount());
        OrderEntity orderEntity = orderMapper.toEntity(createOrderRequest);
        orderEntity.setStatus(OrderStatus.Created);
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        log.info("Order Cerated successfully with userId={}, and amount={}",savedOrder.getUserId(), savedOrder.getAmount());
        return orderMapper.toResponse(savedOrder);


    }

    public OrderResponse findById(Long id){
        log.debug("Fetching Order with id={}", id);
        OrderEntity orderEntity = orderRepository.findById(id).orElseThrow(() -> {
            log.warn("Order with id={} not found", id);
            return new OrderNotFoundException("Order with id=" + id + " not found");
        });
        return orderMapper.toResponse(orderEntity);

    }

    public List<OrderResponse> findAll(){
        log.debug("Fetching all Orders");
        List<OrderEntity> orders = orderRepository.findAll();
        log.info("Fetched {} Orders successfully", orders.size());
        return orders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
    }

    public String deleteById(Long id){
        log.info("Delete order with id={}", id);
        OrderEntity deleteEntity = orderRepository.findById(id).orElseThrow(()->{
            log.warn("For deleting Order with id={} not found", id);
            return new OrderNotFoundException("Order with id=" + id + " not found");
        });
            orderRepository.delete(deleteEntity);
            log.info("Order deleted successfully with orderId={}", id);
            return "Order deleted successfully with orderId=" + id;

    }

    public OrderResponse updateOrder(UpdateOrderRequestDto updateOrderRequestDto, Long id){
        log.info("Updating order with id={}", id);
        if(updateOrderRequestDto.getAmount()==null || updateOrderRequestDto.getAmount().compareTo(BigDecimal.ZERO)<=0){
            log.warn("Trying to update the amount with zero ");
            throw new InvalidOrderException("Amount must be greater than zero");
        }

        OrderEntity getEntity = orderRepository.findById(id).orElseThrow(() ->{
            log.warn(" For update Order with id={} not found", id);
            return new OrderNotFoundException("Order with id=" + id + " not found");
        });
        log.debug("Existing order found with id={}, current status={}", id, getEntity.getStatus());
        if(getEntity.getStatus() == OrderStatus.Cancelled){
            log.warn("Attempt to update cancelled order with status={}",updateOrderRequestDto.getStatus());
            throw new InvalidOrderException("Cancelled Order cannot be updated");
        }

        orderMapper.updateEntity(updateOrderRequestDto,getEntity);

            log.info("Order updated successfully with orderId= {}, new status= {}", id,getEntity.getStatus());
            return orderMapper.toResponse( orderRepository.save(getEntity));

    }


}

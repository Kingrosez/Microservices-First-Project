package com.MFP.OrderService.service;

import com.MFP.OrderService.dto.CreateOrderRequest;
import com.MFP.OrderService.dto.OrderResponse;
import com.MFP.OrderService.dto.UpdateOrderRequestDto;
import com.MFP.OrderService.entity.OrderEntity;
import com.MFP.OrderService.enums.OrderStatus;
import com.MFP.OrderService.exception.InvalidOrderException;
import com.MFP.OrderService.exception.OrderNotFoundException;
import com.MFP.OrderService.mapper.OrderMapper;
import com.MFP.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderMapper orderMapper;
    @Test
    void shouldCreatedOrderSuccessfully(){
        CreateOrderRequest orderRequest = CreateOrderRequest.builder().userId(1L).amount(BigDecimal.valueOf(3000)).build();
        OrderEntity entity = OrderEntity.builder().userId(1L).amount(BigDecimal.valueOf(3000)).status(OrderStatus.Created).build();
        OrderEntity savedEntity= OrderEntity.builder().id(10L).userId(100L).amount(BigDecimal.valueOf(3000)).status(OrderStatus.Created).build();
        OrderResponse orderResponse = OrderResponse.builder().id(10L).userId(100L).amount(BigDecimal.valueOf(3000)).status(OrderStatus.Created).build();

        when(orderMapper.toEntity(orderRequest)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(savedEntity);
        when(orderMapper.toResponse(savedEntity)).thenReturn(orderResponse);

        OrderResponse serviceOrder = orderService.createOrder(orderRequest);
        assertNotNull(serviceOrder);
        assertEquals(10L,serviceOrder.getId());
        verify(orderRepository).save(entity);
    }
    @Test
    void shouldThrowExceptionWhenAmountIsInvalid(){
        CreateOrderRequest orderRequest = CreateOrderRequest.builder().userId(1L).amount(BigDecimal.ZERO).build();
        InvalidOrderException invalidOrderException = assertThrows(InvalidOrderException.class, ()-> orderService.createOrder(orderRequest));
        assertEquals(invalidOrderException.getMessage(),"Amount must be greater than zero`");

        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderMapper);

    }
    @Test
    void shouldThrowExceptionWhenUserIdIsNull(){
        CreateOrderRequest orderRequest = CreateOrderRequest.builder().userId(null).amount(BigDecimal.valueOf(35000)).build();
        InvalidOrderException invalidOrderException = assertThrows(InvalidOrderException.class, () -> orderService.createOrder(orderRequest));
        assertEquals(invalidOrderException.getMessage(),"User id must not be null");
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderMapper);
    }
    @Test
    void shouldUpdateTheOrder(){
        Long orderId = 1L;

        UpdateOrderRequestDto updateOrderRequest = UpdateOrderRequestDto.builder().amount(BigDecimal.valueOf(5000)).status(OrderStatus.Approved).build();
        OrderEntity savedOrder =
            OrderEntity.builder()
                    .id(orderId)
                    .userId(1L)
                    .amount(BigDecimal.valueOf(5000))
                    .status(OrderStatus.Approved)
                    .build();
        OrderResponse response = OrderResponse.builder().id(orderId).amount(BigDecimal.valueOf(5000)).status(OrderStatus.Approved).build();
        OrderEntity existingOrder = OrderEntity.builder().id(orderId).userId(1L).amount(BigDecimal.valueOf(5000)).status(OrderStatus.Created).build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);
        OrderResponse updatedOrder = orderService.updateOrder(updateOrderRequest, orderId);
        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.Approved,updatedOrder.getStatus());
        assertEquals(BigDecimal.valueOf(5000),updatedOrder.getAmount());

        verify(orderRepository).findById(orderId);
        verify(orderMapper).updateEntity(updateOrderRequest,existingOrder);
    }

    @Test
    void shouldThrowOrderNotFoundException(){
        Long orderId = 1L;
        UpdateOrderRequestDto requestDto = UpdateOrderRequestDto.builder()
                .userId(2L)
                .amount(BigDecimal.valueOf(5000))
                .status(OrderStatus.Approved)
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        OrderNotFoundException orderNotFoundException = assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(requestDto, orderId));
        assertEquals("Order with id=1 not found",orderNotFoundException.getMessage());

        verify(orderRepository).findById(orderId);
        verifyNoInteractions(orderMapper);

    }
    @Test
    void shouldThrowExceptionWhenUpdateOrderAmountIsInvalid(){
        UpdateOrderRequestDto updateOrderRequestDto = UpdateOrderRequestDto.builder().userId(1L).amount(BigDecimal.ZERO).build();
        InvalidOrderException invalidOrderException = assertThrows(InvalidOrderException.class, () -> orderService.updateOrder(updateOrderRequestDto, 1L));
        assertEquals("Amount must be greater than zero",invalidOrderException.getMessage());
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderMapper);

    }
@Test
    void shouldThrowExceptionWhenWhenTryToUpdateTheCancelledOrder(){
        UpdateOrderRequestDto updateOrderRequestDto = UpdateOrderRequestDto.builder().userId(1L).amount(BigDecimal.valueOf(5000)).status(OrderStatus.Cancelled).build();
        OrderEntity orderEntity = OrderEntity.builder().id(1L).userId(1L).amount(BigDecimal.valueOf(5000)).status(OrderStatus.Cancelled).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        InvalidOrderException invalidOrderException = assertThrows(InvalidOrderException.class, () -> orderService.updateOrder(updateOrderRequestDto, 1L));
        assertEquals("Cancelled Order cannot be updated",invalidOrderException.getMessage());
        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderMapper);

    }


    @Test
    void shouldDeleteTheOrder(){
        Long orderId = 1L;
        OrderEntity foundedOrder = OrderEntity.builder().id(orderId).userId(1L).amount(BigDecimal.valueOf(5000)).status(OrderStatus.Created).build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(foundedOrder));
        String deleted = orderService.deleteById(orderId);
        assertNotNull(deleted);
        assertEquals(deleted,"Order deleted successfully with orderId=" +orderId);

    }
    @Test
    void shouldDeleteThrowExceptionWhenOrderIdNotFound(){
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        OrderNotFoundException orderNotFoundException = assertThrows(OrderNotFoundException.class, () -> orderService.deleteById(1L));
        assertEquals(orderNotFoundException.getMessage(),"Order with id=1 not found");

        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderMapper);
    }
    @Test
    void shouldGetOrderById(){
        OrderEntity orderEntity = OrderEntity.builder().id(1L).userId(2L).amount(BigDecimal.valueOf(35000)).status(OrderStatus.Created).build();
        OrderResponse orderResponse = OrderResponse.builder().id(1L).userId(2L).amount(BigDecimal.valueOf(5000)).status(OrderStatus.Created).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toResponse(orderEntity)).thenReturn(orderResponse);
        OrderResponse serviceOrder = orderService.findById(1L);
        assertNotNull(serviceOrder);
        assertEquals(orderResponse,serviceOrder);
        verify(orderRepository).findById(1L);
    }
    @Test
    void shouldGetOrderByIdThrowException(){
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        OrderNotFoundException orderNotFoundException = assertThrows(OrderNotFoundException.class, () -> orderService.findById(1L));
        assertEquals(orderNotFoundException.getMessage(),"Order with id=1 not found");
        verify(orderRepository).findById(1L);
    }
    @Test
    void shouldGetListOfOrders(){
        OrderEntity orderEntity1 = OrderEntity.builder().id(1L).userId(1L).amount(BigDecimal.valueOf(2500)).status(OrderStatus.Created).build();
        OrderEntity orderEntity2 = OrderEntity.builder().id(2L).userId(2L).amount(BigDecimal.valueOf(3500)).status(OrderStatus.Approved).build();
        List<OrderEntity>  orderEntities = Arrays.asList(orderEntity1,orderEntity2);
        when(orderRepository.findAll()).thenReturn(orderEntities);
        OrderResponse orderResponse1 = OrderResponse.builder().id(1L).userId(1L).amount(BigDecimal.valueOf(2500)).status(OrderStatus.Created).build();
        OrderResponse orderResponse2 = OrderResponse.builder().id(2L).userId(2L).amount(BigDecimal.valueOf(3500)).status(OrderStatus.Approved).build();

        when(orderMapper.toResponse(orderEntity1)).thenReturn(orderResponse1);
        when(orderMapper.toResponse(orderEntity2)).thenReturn(orderResponse2);
        List<OrderResponse> responseList = orderService.findAll();
        assertNotNull(responseList);
        assertEquals(2,responseList.size());
        assertEquals(orderResponse1,responseList.get(0));
        assertEquals(orderResponse2,responseList.get(1));
        verify(orderRepository).findAll();

    }
}

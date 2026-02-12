package com.MFP.OrderService.controller;

import com.MFP.OrderService.dto.CreateOrderRequest;
import com.MFP.OrderService.dto.OrderResponse;
import com.MFP.OrderService.dto.UpdateOrderRequestDto;
import com.MFP.OrderService.enums.OrderStatus;
import com.MFP.OrderService.exception.OrderNotFoundException;
import com.MFP.OrderService.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(25000))
                .build();

        OrderResponse orderResponse =
            OrderResponse.builder()
                    .id(1L)
                    .userId(1L)
                    .amount(BigDecimal.valueOf(25000))
                    .status(OrderStatus.Created)
                    .build();

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);
        mockMvc.perform(post("https://localhost:8082/api/order/v1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("Created"));
    }
    @Test
    void shouldReturnBadRequestWhenUserIdIsNull() throws Exception {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .amount(BigDecimal.valueOf(25000))
                .build();

        mockMvc.perform(post("https://localhost:8082/api/order/v1").
                contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(orderService);
    }
    @Test
    void shouldReturnNotFoundExceptionWhenOrderIdDoesNotExist() throws Exception {
        when(orderService.updateOrder(any(UpdateOrderRequestDto.class),eq(1L)))
                .thenThrow(new OrderNotFoundException("Order with id=1 not found"));
        UpdateOrderRequestDto updateOrderRequestDto=
                UpdateOrderRequestDto.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2000))
                        .status(OrderStatus.Created)
                        .build();
        mockMvc.perform(put("/api/order/v1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateOrderRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with id=1 not found"));
    }

    @Test
    void shouldSuccessfullyDeleteTheOrder() throws Exception {
        when(orderService.deleteById(1L)).thenReturn("Deleted successfully");
        mockMvc.perform(delete("/api/order/v1/1")).andExpect(status().isOk());
        verify(orderService).deleteById(1L);

    }
    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {
        OrderResponse orderResponse=
                OrderResponse.builder()
                        .id(1L)
                        .userId(2L)
                        .amount(BigDecimal.valueOf(3250))
                        .status(OrderStatus.Created)
                        .build();
        when(orderService.findById(1L)).thenReturn(orderResponse);
        mockMvc.perform(get("/api/order/v1/{id}",1L))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("Created"));
        verify(orderService).findById(1L);
    }

    @Test
    void shouldGetOrderByIdThrowExceptionNotFoundException() throws Exception {
        when(orderService.findById(1L)).thenThrow(new OrderNotFoundException("Order with id=1 not found"));
        mockMvc.perform(get("/api/order/v1/{id}",1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with id=1 not found"));
        verify(orderService).findById(1L);
    }
    @Test
    void shouldGetAllOrdersSuccessfully() throws Exception {
    OrderResponse orderResponse1=
            OrderResponse.builder()
                    .id(1L)
                    .userId(2L)
                    .amount(BigDecimal.valueOf(2458))
                    .status(OrderStatus.Created)
                    .build();

        OrderResponse orderResponse2=
                OrderResponse.builder()
                        .id(2L)
                        .userId(2L)
                        .amount(BigDecimal.valueOf(2458))
                        .status(OrderStatus.Approved)
                        .build();

        when(orderService.findAll()).thenReturn(Arrays.asList(orderResponse1,orderResponse2));

        mockMvc.perform(get("/api/order/v1"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void shouldUpdateOrderSuccessfully() throws Exception {
        UpdateOrderRequestDto orderRequestDto=
                UpdateOrderRequestDto.builder()
                        .userId(2L)
                        .amount(BigDecimal.valueOf(2589))
                        .status(OrderStatus.Created)
                        .build();
        OrderResponse orderResponse=
                OrderResponse.builder()
                        .id(1L)
                        .userId(2L)
                        .amount(BigDecimal.valueOf(14586))
                        .status(OrderStatus.Created)
                        .build();

        when(orderService.updateOrder(any(UpdateOrderRequestDto.class),eq(1L))).thenReturn(orderResponse);
        mockMvc.perform(put("/api/order/v1/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(14586));

        verify(orderService).updateOrder(any(UpdateOrderRequestDto.class),eq(1L));
    }
}

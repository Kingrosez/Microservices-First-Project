package com.MFP.OrderService.integration;

import com.MFP.OrderService.dto.CreateOrderRequest;
import com.MFP.OrderService.dto.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Should Create a order and return success code 200")
    void shouldCreateOrder() throws Exception {
        CreateOrderRequest orderRequest =
                CreateOrderRequest.builder()
                        .userId(1L)
                        .amount(BigDecimal.valueOf(2522))
                        .build();

        mockMvc.perform(post("/api/order/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.amount").value(2522))
                .andExpect(jsonPath("$.status").value("Created"));
    }
    @Test
    @DisplayName("Should return the Order by id successfully")
    void shouldReturnOrderById() throws Exception {
        CreateOrderRequest orderRequest=
                CreateOrderRequest.builder()
                        .userId(2L)
                        .amount(BigDecimal.valueOf(2586))
                        .build();
        String response = mockMvc.perform(post("/api/order/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponse orderResponse = objectMapper.readValue(response, OrderResponse.class);

        mockMvc.perform(get("/api/order/v1/{id}",orderResponse.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.userId").value(2L));

    }
    @Test
    @DisplayName("Should return the exception when amount is null")
    void shouldFailWhenAmountIsNull() throws Exception {
      CreateOrderRequest orderRequest=
              CreateOrderRequest.builder()
                      .userId(2L)
                      .build();

      mockMvc.perform(post("/api/order/v1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(orderRequest)))
              .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return the exception when order not found with id")
    void shouldFailWhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/api/order/v1/{id}",2L))
                .andExpect(status().isNotFound());
    }
}

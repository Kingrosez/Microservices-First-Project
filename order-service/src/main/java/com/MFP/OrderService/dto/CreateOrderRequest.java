package com.MFP.OrderService.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    @NotNull(message = "User Id must not be null")
    private Long userId;
    @NotNull(message = "Amount must Not be null")
    @DecimalMin(value = "0.1",message = "Amount must be grater than zero")
    private BigDecimal amount;
}

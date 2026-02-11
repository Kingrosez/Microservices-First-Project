package com.MFP.OrderService.dto;

import com.MFP.OrderService.enums.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderRequestDto {
    @NotNull(message = "User id must not be null")
    private Long userId;
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.1",message = "Amount must be greater than zero")
    private BigDecimal amount;
    @NotNull(message = "Order status must not be null")
    private OrderStatus status;
}

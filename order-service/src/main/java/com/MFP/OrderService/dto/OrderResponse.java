package com.MFP.OrderService.dto;

import com.MFP.OrderService.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private OrderStatus status;
}

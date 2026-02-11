package com.MFP.OrderService.controller;

import com.MFP.OrderService.dto.CreateOrderRequest;
import com.MFP.OrderService.dto.OrderResponse;
import com.MFP.OrderService.dto.UpdateOrderRequestDto;
import com.MFP.OrderService.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("api/order/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        System.out.println(createOrderRequest.getUserId());
        log.info("POST /order request received ");
        OrderResponse createdOrder = orderService.createOrder(createOrderRequest);
        log.info("POST / Create order successfully with orderId={} ",createdOrder.getId() );
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrderById(@Valid @RequestBody UpdateOrderRequestDto updateOrderRequestDto, @PathVariable Long id) {
        log.info("PUT /order request Updated with id {}", id);
        OrderResponse updatedOrder = orderService.updateOrder(updateOrderRequestDto, id);
        log.info("PUT /Update order successfully with id={} ", updatedOrder.getId());
        return new ResponseEntity<>(updatedOrder, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable Long id) {
        log.info("DELETE /order request Delete with id {}", id);
        String deleted = orderService.deleteById(id);
        log.info("DELETE /Delete order successfully with id={} ", deleted);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        log.info("GET /order request fetch by id={}",id);
        OrderResponse orderResponse = orderService.findById(id);
        log.info("GET /Order successfully fetched with id={} ", orderResponse.getId());
        return new ResponseEntity<>(orderResponse, HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> gatAllOrders() {
        log.info("GET /All Orders fetched");
        List<OrderResponse> ordersResponse = orderService.findAll();
        log.info("GET /{} Orders successfully fetched", ordersResponse.size());
        return new ResponseEntity<>(ordersResponse, HttpStatus.FOUND);
    }
}

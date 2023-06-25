package com.orcaaa.controller;

import com.orcaaa.dtos.request.OrderRequest;
import com.orcaaa.dtos.response.OrderResponse;
import com.orcaaa.exception.ResourceNotFoundException;
import com.orcaaa.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            return orderService.createOrder(orderRequest);
        } catch (ResourceNotFoundException ex){
            log.error("Error in createOrder method: {}", ex.getMessage());
            throw ex;
        }catch (Exception e) {
            log.error("Error in createOrder method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

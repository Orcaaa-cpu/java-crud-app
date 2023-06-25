package com.orcaaa.service;

import com.orcaaa.dtos.request.OrderRequest;
import com.orcaaa.entity.Customer;
import com.orcaaa.entity.Order;
import com.orcaaa.entity.Product;
import com.orcaaa.dtos.response.OrderResponse;
import com.orcaaa.exception.ResourceNotFoundException;
import com.orcaaa.repository.CustomerRepository;
import com.orcaaa.repository.OrderRepository;
import com.orcaaa.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    @Transactional
    public ResponseEntity<OrderResponse> createOrder(OrderRequest orderRequest) {
        String customerId = orderRequest.getCustomerId();
        String productId = orderRequest.getProductId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        BigDecimal productPrice = new BigDecimal(product.getProductPrice());
        int quantity = orderRequest.getQuantity();
        BigDecimal amount = productPrice.multiply(BigDecimal.valueOf(quantity));

        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customer(customer)
                .customerName(customer.getCustomerName())
                .product(product)
                .quantity(quantity)
                .amount(amount)
                .orderDate(LocalDateTime.now())
                .build();

        // Simulate failure scenario
        if (orderRequest.isFailureScenario()) {
            throw new RuntimeException("Failed to create order.");
        }
        if ( order.getQuantity() <= product.getStock() && product.getStock() != 0 ){
            product.setStock(product.getStock() - order.getQuantity());
            productRepository.save(product);
        }else{
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setMessage("Jumlah Stock Product Tidak Mencukupi.");
            orderResponse.setStatus(HttpStatus.BAD_REQUEST.toString());

                return new ResponseEntity<>(orderResponse, HttpStatus.BAD_REQUEST);
        }

        orderRepository.save(order);
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setMessage("Berhasil Order Product.");
        orderResponse.setStatus(HttpStatus.OK.toString());
        orderResponse.setOrder(order);

        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
}

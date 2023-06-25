package com.orcaaa.dtos.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Object data;
    private String message;
    private String status;

    public OrderResponse(String message, String status, Object data) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public static OrderResponse orderResponse(String message, String status, Object obj) {
        OrderResponse orderResponse = OrderResponse.builder()
                .status(status)
                .message(message)
                .data(obj)
                .build();
        return orderResponse;
    }
}

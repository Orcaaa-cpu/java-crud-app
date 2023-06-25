package com.orcaaa.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class ProductResponse {
    private String message;
    private Object data;

    public ProductResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public static ProductResponse productResponse(String str, Object obj){
        ProductResponse productRespons = ProductResponse.builder()
                .data(obj)
                .message(str)
                .build();
        return productRespons;
    }
}

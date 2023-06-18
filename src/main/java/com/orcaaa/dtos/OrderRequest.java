package com.orcaaa.dtos;

import lombok.Data;

@Data

public class OrderRequest {
    private String customerId;
    private String productId;
    private int quantity;
    private boolean failureScenario;

    // constructors, getters, setters
}

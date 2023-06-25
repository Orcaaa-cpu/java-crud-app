package com.orcaaa.dtos.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CustomerRespon {
    private String message;
    private Object data;

    public CustomerRespon(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}

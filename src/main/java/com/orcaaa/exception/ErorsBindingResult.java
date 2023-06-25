package com.orcaaa.exception;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class ErorsBindingResult {
    public static List<String> erorsBindingResult(BindingResult bindingResult){
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        return  errors;
    }
}

package com.orcaaa.controller;

import com.orcaaa.entity.Product;
import com.orcaaa.dtos.response.ProductResponse;
import com.orcaaa.exception.ErorsBindingResult;
import com.orcaaa.exception.ResourceNotFoundException;
import com.orcaaa.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ProductResponse.productResponse("Validation Failed", ErorsBindingResult.erorsBindingResult(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try {
            log.info("Entering addProduct method: {}", product);
            return productService.addProduct(product);
        } catch (Exception e) {
            log.error("Error in addProduct method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> editProduct(@PathVariable String productId, @Valid @RequestBody Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ProductResponse.productResponse("Validation Failed", ErorsBindingResult.erorsBindingResult(bindingResult)), HttpStatus.BAD_REQUEST);
        }

        try {
            log.info("Entering editProduct method: productId={}, product={}", productId, product);
            return productService.editProduct(productId, product);
        } catch (ResourceNotFoundException e) {
            log.error("Error in editProduct method: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error in editProduct method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        try {
            log.info("Entering getProductById method: productId={}", productId);
            return productService.getProductById(productId);
        } catch (ResourceNotFoundException ex){
            log.error("Error in getProductById method: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Error in getProductById method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}


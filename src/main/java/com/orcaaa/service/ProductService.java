package com.orcaaa.service;

import com.orcaaa.dtos.response.ProductResponse;
import com.orcaaa.entity.Product;
import com.orcaaa.exception.ResourceNotFoundException;
import com.orcaaa.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ResponseEntity<ProductResponse> addProduct(Product product) {
        validateProductRequest(product);
        try {
            productRepository.save(product);
            return new ResponseEntity<>(ProductResponse.productResponse("Success Add Data Product", product), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(ProductResponse.productResponse("Failed to add product", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ProductResponse> editProduct(String productId, Product product) {
        validateProductRequest(product);

        try {
            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            existingProduct.setProductPrice(product.getProductPrice());
            existingProduct.setProductDescription(product.getProductDescription());
            existingProduct.setStock(product.getStock());

            productRepository.save(existingProduct);

            return new ResponseEntity<>(ProductResponse.productResponse("Success Edit Data Product", existingProduct), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update product", ex);
        }
    }

    public ResponseEntity<ProductResponse> getProductById(String productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            return new ResponseEntity<>(ProductResponse.productResponse("Success Get Data Product", product), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get product by ID", e);
        }
    }

    private void validateProductRequest(Product product) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                    .collect(Collectors.toList());

            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
            for (String error : errors) {
                FieldError fieldError = new FieldError("product", "", error);
                bindingResult.addError(fieldError);
            }

            try {
                throw new MethodArgumentNotValidException(
                        new MethodParameter(ProductService.class.getDeclaredMethod("addProduct", Product.class), 0),
                        bindingResult);

            } catch (NoSuchMethodException | MethodArgumentNotValidException e) {
                throw new RuntimeException("Failed to validate product request", e);
            }
        }
    }
}

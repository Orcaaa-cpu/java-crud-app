package com.orcaaa.service;

import com.orcaaa.dtos.response.CustomerRespon;
import com.orcaaa.entity.Customer;
import com.orcaaa.exception.ResourceNotFoundException;
import com.orcaaa.repository.CustomerRepository;
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
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public ResponseEntity<CustomerRespon> addCustomer(Customer customer) {
        validateCustomerRequest(customer);
        try {
            customerRepository.save(customer);
            return new ResponseEntity<>( CustomerRespon.customerRespons("Success Add Data Customer", customer), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(CustomerRespon.customerRespons("Failed to add customer", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<CustomerRespon> editCustomer(String customerId, Customer customer) {
        validateCustomerRequest(customer);

        try {
            Customer existingCustomer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            existingCustomer.setCustomerName(customer.getCustomerName());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setPhone(customer.getPhone());

            customerRepository.save(existingCustomer);

            return new ResponseEntity<>(CustomerRespon.customerRespons("Success Edit Data Customer", existingCustomer), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update customer", ex);
        }
    }

    public ResponseEntity<CustomerRespon> getCustomerById(String customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            return new ResponseEntity<>(CustomerRespon.customerRespons("Success Get Data Customer",customer), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customer by ID", e);
        }
    }

    private void validateCustomerRequest(Customer customer) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                    .collect(Collectors.toList());

            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(customer, "customer");
            for (String error : errors) {
                FieldError fieldError = new FieldError("customer", "", error);
                bindingResult.addError(fieldError);
            }

            try {
                throw new MethodArgumentNotValidException(
                        new MethodParameter(CustomerService.class.getDeclaredMethod("addCustomer", Customer.class), 0),
                        bindingResult);

            } catch (NoSuchMethodException | MethodArgumentNotValidException e) {
                throw new RuntimeException("Failed to validate customer request", e);
            }
        }
    }
}

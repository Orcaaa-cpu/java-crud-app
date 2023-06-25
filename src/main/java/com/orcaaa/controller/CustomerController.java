package com.orcaaa.controller;

import com.orcaaa.entity.Customer;
import com.orcaaa.dtos.response.CustomerRespon;
import com.orcaaa.exception.ErorsBindingResult;
import com.orcaaa.exception.ResourceNotFoundException;
import com.orcaaa.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerRespon> addCustomer(@Valid @RequestBody Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(CustomerRespon.customerRespons("Validation Failed", ErorsBindingResult.erorsBindingResult(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try {
            log.info("Entering addCustomer method: {}", customer);
            return customerService.addCustomer(customer);
        } catch (Exception e) {
            log.error("Error in addCustomer method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerRespon> editCustomer(@PathVariable String customerId, @Valid @RequestBody Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(CustomerRespon.customerRespons("Validation Failed", ErorsBindingResult.erorsBindingResult(bindingResult)), HttpStatus.BAD_REQUEST);
        }

        try {
            log.info("Entering editCustomer method: customerId={}, customer={}", customerId, customer);
            return customerService.editCustomer(customerId, customer);
        } catch (ResourceNotFoundException e) {
            log.error("Error in editCustomer method: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error in editCustomer method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerRespon> getCustomerById(@PathVariable String customerId) {
        try {
            log.info("Entering getCustomerById method: customerId={}", customerId);
            return customerService.getCustomerById(customerId);
        } catch (ResourceNotFoundException ex){
            log.error("Error in getCustomerById method: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Error in getCustomerById method: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

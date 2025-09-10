package com.example.banking_api_challenge.controller;

import com.example.banking_api_challenge.model.Customer;
import com.example.banking_api_challenge.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers")
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerRepository customerRepository;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by their ID")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long customerId) {
        return customerRepository.findById(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

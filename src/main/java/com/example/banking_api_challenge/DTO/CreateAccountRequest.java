package com.example.banking_api_challenge.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.01", message = "Initial deposit must be positive")
    private BigDecimal initialDeposit;
}
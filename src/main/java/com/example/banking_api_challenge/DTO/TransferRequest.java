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
public class TransferRequest {
    @NotNull(message = "From account is required")
    private Long fromAccountId;

    @NotNull(message = "To account is required")
    private Long toAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be positive")
    private BigDecimal amount;

    private String description;

}
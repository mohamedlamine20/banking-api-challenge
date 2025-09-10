package com.example.banking_api_challenge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Long customerId;
    private String customerName;
    private LocalDateTime createdAt;
}
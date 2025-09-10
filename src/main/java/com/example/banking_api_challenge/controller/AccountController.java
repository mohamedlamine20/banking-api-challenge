package com.example.banking_api_challenge.controller;

import com.example.banking_api_challenge.DTO.AccountResponse;
import com.example.banking_api_challenge.DTO.CreateAccountRequest;
import com.example.banking_api_challenge.DTO.TransferRequest;
import com.example.banking_api_challenge.DTO.TransferResponse;
import com.example.banking_api_challenge.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {


    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new bank account", description = "Creates a new bank account for a customer with an initial deposit")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse account = accountService.createAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get account balance", description = "Retrieves the current balance for a given account")
    public ResponseEntity<AccountResponse> getAccountBalance(
            @Parameter(description = "Account ID") @PathVariable Long accountId) {
        AccountResponse account = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds", description = "Transfers money between two accounts")
    public ResponseEntity<TransferResponse> transferFunds(@Valid @RequestBody TransferRequest request) {
        TransferResponse transfer = accountService.transferFunds(request);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/transfers")
    @Operation(summary = "Get transfer history", description = "Retrieves the transfer history for a given account")
    public ResponseEntity<List<TransferResponse>> getTransferHistory(
            @Parameter(description = "Account ID") @PathVariable Long accountId) {
        List<TransferResponse> transfers = accountService.getTransferHistory(accountId);
        return ResponseEntity.ok(transfers);
    }

}

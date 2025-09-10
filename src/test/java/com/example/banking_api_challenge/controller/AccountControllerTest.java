package com.example.banking_api_challenge.controller;

import com.example.banking_api_challenge.DTO.AccountResponse;
import com.example.banking_api_challenge.DTO.CreateAccountRequest;
import com.example.banking_api_challenge.DTO.TransferRequest;
import com.example.banking_api_challenge.DTO.TransferResponse;
import com.example.banking_api_challenge.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_Success() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(1L, new BigDecimal("500.00"));
        AccountResponse response = new AccountResponse(1L, "ACC-12345", new BigDecimal("500.00"), 1L, "Test Customer", LocalDateTime.now());

        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("ACC-12345"))
                .andExpect(jsonPath("$.balance").value(500.00))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("Test Customer"));
    }

    @Test
    void createAccount_ValidationError() throws Exception {
        // Given - Invalid request with negative initial deposit
        CreateAccountRequest request = new CreateAccountRequest(1L, new BigDecimal("-100.00"));

        // When & Then
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void getAccountBalance_Success() throws Exception {
        // Given
        AccountResponse response = new AccountResponse(1L, "ACC-12345", new BigDecimal("1000.00"), 1L, "Test Customer", LocalDateTime.now());
        when(accountService.getAccountBalance(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/accounts/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void transferFunds_Success() throws Exception {
        // Given
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"), "Test transfer");
        TransferResponse response = new TransferResponse(1L, 1L, 2L, new BigDecimal("100.00"), "Test transfer", LocalDateTime.now());

        when(accountService.transferFunds(any(TransferRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fromAccountId").value(1))
                .andExpect(jsonPath("$.toAccountId").value(2))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getTransferHistory_Success() throws Exception {
        // Given
        TransferResponse transfer1 = new TransferResponse(1L, 1L, 2L, new BigDecimal("100.00"), "Transfer 1", LocalDateTime.now());
        TransferResponse transfer2 = new TransferResponse(2L, 2L, 1L, new BigDecimal("50.00"), "Transfer 2", LocalDateTime.now());
        List<TransferResponse> transfers = Arrays.asList(transfer1, transfer2);

        when(accountService.getTransferHistory(1L)).thenReturn(transfers);

        // When & Then
        mockMvc.perform(get("/api/accounts/1/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
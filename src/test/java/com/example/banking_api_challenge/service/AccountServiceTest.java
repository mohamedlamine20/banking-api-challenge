package com.example.banking_api_challenge.service;

import com.example.banking_api_challenge.DTO.AccountResponse;
import com.example.banking_api_challenge.DTO.CreateAccountRequest;
import com.example.banking_api_challenge.DTO.TransferRequest;
import com.example.banking_api_challenge.DTO.TransferResponse;
import com.example.banking_api_challenge.exception.AccountNotFoundException;
import com.example.banking_api_challenge.exception.CustomerNotFoundException;
import com.example.banking_api_challenge.exception.InsufficientFundsException;
import com.example.banking_api_challenge.model.Account;
import com.example.banking_api_challenge.model.Customer;
import com.example.banking_api_challenge.model.Transfer;
import com.example.banking_api_challenge.repository.AccountRepository;
import com.example.banking_api_challenge.repository.CustomerRepository;
import com.example.banking_api_challenge.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer testCustomer;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "Test Customer",null);
        testAccount = new Account("ACC-12345", new BigDecimal("1000.00"), testCustomer);
        testAccount.setId(1L);
    }

    @Test
    void createAccount_Success() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(1L, new BigDecimal("500.00"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // When
        AccountResponse response = accountService.createAccount(request);

        // Then
        assertNotNull(response);
        assertEquals(testAccount.getId(), response.getId());
        assertEquals(testAccount.getBalance(), response.getBalance());
        assertEquals(testCustomer.getId(), response.getCustomerId());
        assertEquals(testCustomer.getName(), response.getCustomerName());
        verify(customerRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_CustomerNotFound() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(999L, new BigDecimal("500.00"));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> accountService.createAccount(request));
        verify(customerRepository).findById(999L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountBalance_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        AccountResponse response = accountService.getAccountBalance(1L);

        // Then
        assertNotNull(response);
        assertEquals(testAccount.getId(), response.getId());
        assertEquals(testAccount.getBalance(), response.getBalance());
        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccountBalance_AccountNotFound() {
        // Given
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountBalance(999L));
        verify(accountRepository).findById(999L);
    }

    @Test
    void transferFunds_Success() {
        // Given
        Account toAccount = new Account("ACC-67890", new BigDecimal("500.00"), testCustomer);
        toAccount.setId(2L);

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"), "Test transfer");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        Transfer savedTransfer = new Transfer(testAccount, toAccount, new BigDecimal("100.00"), "Test transfer");
        savedTransfer.setId(1L);
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        // When
        TransferResponse response = accountService.transferFunds(request);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("900.00"), testAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), toAccount.getBalance());
        assertEquals(savedTransfer.getId(), response.getId());
        assertEquals(savedTransfer.getAmount(), response.getAmount());

        verify(accountRepository).save(testAccount);
        verify(accountRepository).save(toAccount);
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void transferFunds_InsufficientFunds() {
        // Given
        Account toAccount = new Account("ACC-67890", new BigDecimal("500.00"), testCustomer);
        toAccount.setId(2L);

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("1500.00"), "Test transfer");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        // When & Then
        assertThrows(InsufficientFundsException.class, () -> accountService.transferFunds(request));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void transferFunds_FromAccountNotFound() {
        // Given
        TransferRequest request = new TransferRequest(999L, 2L, new BigDecimal("100.00"), "Test transfer");
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> accountService.transferFunds(request));
        verify(accountRepository).findById(999L);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void getTransferHistory_Success() {
        // Given
        Account toAccount = new Account("ACC-67890", new BigDecimal("500.00"), testCustomer);
        toAccount.setId(2L);

        Transfer transfer1 = new Transfer(testAccount, toAccount, new BigDecimal("100.00"), "Transfer 1");
        transfer1.setId(1L);
        Transfer transfer2 = new Transfer(toAccount, testAccount, new BigDecimal("50.00"), "Transfer 2");
        transfer2.setId(2L);

        List<Transfer> transfers = Arrays.asList(transfer1, transfer2);

        when(accountRepository.existsById(1L)).thenReturn(true);
        when(transferRepository.findTransferHistoryByAccountId(1L)).thenReturn(transfers);

        // When
        List<TransferResponse> responses = accountService.getTransferHistory(1L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(transfer1.getId(), responses.get(0).getId());
        assertEquals(transfer2.getId(), responses.get(1).getId());

        verify(accountRepository).existsById(1L);
        verify(transferRepository).findTransferHistoryByAccountId(1L);
    }

    @Test
    void getTransferHistory_AccountNotFound() {
        // Given
        when(accountRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> accountService.getTransferHistory(999L));
        verify(accountRepository).existsById(999L);
        verify(transferRepository, never()).findTransferHistoryByAccountId(anyLong());
    }
}
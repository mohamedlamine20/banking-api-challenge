package com.example.banking_api_challenge.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;


    private final CustomerRepository customerRepository;


    private final TransferRepository transferRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + request.getCustomerId()));

        String accountNumber = generateAccountNumber();
        Account account = Account.builder().accountNumber(accountNumber).balance(request.getInitialDeposit()).customer(customer)
                .createdAt(java.time.LocalDateTime.now()).build();
        Account savedAccount = accountRepository.save(account);

        return mapToAccountResponse(savedAccount);
    }

    public AccountResponse getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new com.example.banking_api_challenge.exception.AccountNotFoundException("Account not found with ID: " + accountId));

        return mapToAccountResponse(account);
    }

    @Transactional
    public TransferResponse transferFunds(TransferRequest request) {
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found with ID: " + request.getFromAccountId()));

        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found with ID: " + request.getToAccountId()));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + fromAccount.getAccountNumber());
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create transfer record
        Transfer transfer = Transfer.builder().fromAccount(fromAccount).toAccount(toAccount)
                .amount(request.getAmount()).timestamp(java.time.LocalDateTime.now())
                .description(request.getDescription()).build();
        Transfer savedTransfer = transferRepository.save(transfer);

        return mapToTransferResponse(savedTransfer);
    }

    public List<TransferResponse> getTransferHistory(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        }

        List<Transfer> transfers = transferRepository.findTransferHistoryByAccountId(accountId);
        return transfers.stream()
                .map(this::mapToTransferResponse)
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AccountResponse mapToAccountResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCustomer().getId(),
                account.getCustomer().getName(),
                account.getCreatedAt()
        );
    }

    private TransferResponse mapToTransferResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getFromAccount().getId(),
                transfer.getToAccount().getId(),
                transfer.getAmount(),
                transfer.getDescription(),
                transfer.getTimestamp()
        );
    }
}


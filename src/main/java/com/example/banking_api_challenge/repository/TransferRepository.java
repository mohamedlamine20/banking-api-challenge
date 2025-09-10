package com.example.banking_api_challenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.banking_api_challenge.model.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {


    @Query("SELECT t FROM Transfer t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.timestamp DESC")
    List<Transfer> findTransferHistoryByAccountId(@Param("accountId") Long accountId);
}

package com.example.project.service;

import com.example.project.dto.TransactionDTO;
import com.example.project.dto.TransferResponse;
import com.example.project.entity.Account;
import com.example.project.entity.Transaction;
import com.example.project.exception.CustomBadRequestException;
import com.example.project.exception.CustomNotFoundException;
import com.example.project.mapper.TransactionMapper;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountService accountService;

    private UUID sourceAccountId;
    private UUID targetAccountId;
    private UUID transactionId;

    @BeforeEach
    public void setUp() {
        sourceAccountId = UUID.randomUUID();
        targetAccountId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
    }

    @Test
    public void testTransferMoney_SuccessfulTransfer() {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setBalance(new BigDecimal("100.00"));

        Account targetAccount = new Account();
        targetAccount.setId(targetAccountId);
        targetAccount.setBalance(new BigDecimal("50.00"));

        TransactionDTO transactionDTO = new TransactionDTO(sourceAccountId, targetAccountId, new BigDecimal("30.00"), null, 0);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));

        Transaction transaction = new Transaction();
        transaction.setId(transactionId); // Assuming transaction has an ID
        transaction.setAmount(new BigDecimal("30.00"));

        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransferResponse result = accountService.transferMoney(transactionDTO);

        // Assert
        verify(accountRepository, times(1)).findById(sourceAccountId);
        verify(accountRepository, times(1)).findById(targetAccountId);
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        // Check balances are updated correctly
        assertEquals(new BigDecimal("70.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("80.00"), targetAccount.getBalance());

        // Check the response
        assertEquals("Transfer successful", result.status());
        assertEquals(transactionDTO.amount(), result.amount());
    }

    @Test
    public void testTransferMoney_InsufficientBalance() {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setBalance(new BigDecimal("20.00"));

        Account targetAccount = new Account();
        targetAccount.setId(targetAccountId);
        targetAccount.setBalance(new BigDecimal("50.00"));

        TransactionDTO transactionDTO = new TransactionDTO(sourceAccountId, targetAccountId, new BigDecimal("30.00"), null, 0);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));

        // Act & Assert
        CustomBadRequestException exception = assertThrows(CustomBadRequestException.class, () ->
                accountService.transferMoney(transactionDTO));

        // Verify the error message
        assertEquals("Insufficient balance in the source account.", exception.getMessage());

        // Ensure the save method was never called due to the exception
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoney_SourceAccountNotFound() {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO(sourceAccountId, targetAccountId, new BigDecimal("30.00"), null, 0);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomNotFoundException exception = assertThrows(CustomNotFoundException.class, () ->
                accountService.transferMoney(transactionDTO));

        // Verify the error message
        assertEquals("Source Account not found with ID: ", exception.getMessage());

        // Verify no further interactions
        verify(accountRepository, times(1)).findById(sourceAccountId);
        verify(accountRepository, never()).findById(targetAccountId);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoney_TargetAccountNotFound() {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setBalance(new BigDecimal("100.00"));

        TransactionDTO transactionDTO = new TransactionDTO(sourceAccountId, targetAccountId, new BigDecimal("30.00"), null, 0);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomNotFoundException exception = assertThrows(CustomNotFoundException.class, () ->
                accountService.transferMoney(transactionDTO));

        // Verify the error message
        assertEquals("Target Account not found with ID: ", exception.getMessage());

        // Verify that the source account was found, but the target account was not
        verify(accountRepository, times(1)).findById(sourceAccountId);
        verify(accountRepository, times(1)).findById(targetAccountId);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoney_SameSourceAndTargetAccount() {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO(sourceAccountId, sourceAccountId, new BigDecimal("30.00"), null, 0);

        // Act & Assert
        CustomBadRequestException exception = assertThrows(CustomBadRequestException.class, () ->
                accountService.transferMoney(transactionDTO));

        // Verify the error message
        assertEquals("Source and target account IDs cannot be the same.", exception.getMessage());

        // Verify no repository interactions were performed
        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
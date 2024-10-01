package com.example.project.service;

import com.example.project.dto.AccountDTO;
import com.example.project.dto.TransactionDTO;
import com.example.project.dto.TransferResponse;
import com.example.project.entity.Account;
import com.example.project.entity.Transaction;
import com.example.project.exception.CustomBadRequestException;
import com.example.project.exception.CustomNotFoundException;
import com.example.project.mapper.AccountMapper;
 import com.example.project.mapper.TransactionMapper;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing accounts and transactions.
 * Provides methods to create, update, retrieve, delete accounts,
 * and transfer money between accounts.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    /**
     * Constructs an AccountService with the given repositories and mappers.
     *
     * @param accountRepository     the repository for managing accounts
     * @param transactionRepository the repository for managing transactions
     * @param accountMapper         the mapper for mapping Account entities and DTOs
     * @param transactionMapper     the mapper for mapping Transaction entities and DTOs
     */
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                          AccountMapper accountMapper, TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountMapper = accountMapper;  // use correct case
        this.transactionMapper = transactionMapper;  // use correct case
    }

    /**
     * Creates a new account and saves it to the repository.
     * Rolls back if the operation fails.
     *
     * @param accountDTO the account data transfer object containing account details
     * @return the created account as a DTO
     */
    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account accountEntity = accountMapper.toEntity(accountDTO);
        Account savedAccount = accountRepository.save(accountEntity);
        return accountMapper.toDto(savedAccount);
    }

    /**
     * Retrieves all accounts from the repository.
     *
     * @return a list of all accounts as DTOs
     */
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param id the UUID of the account to retrieve
     * @return the account as a DTO, or null if not found
     */
    @Transactional(readOnly = true)
    public AccountDTO getAccount(UUID id) {
        Account accountEntity = accountRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Account not found with ID: " + id));
        return accountMapper.toDto(accountEntity);
    }

    /**
     * Updates an existing account's balance.
     * Rolls back if the account is not found or if the operation fails.
     *
     * @param id         the UUID of the account to update
     * @param accountDTO the account DTO containing updated details
     * @throws RuntimeException if the account is not found
     */
    @Transactional
    public void updateAccount(UUID id, AccountDTO accountDTO) {
        Account accountEntity = accountRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Account not found with ID: " + id));
        accountEntity.setBalance(accountDTO.balance());
        accountRepository.save(accountEntity);
    }

    /**
     * Deletes an account by its ID.
     * Rolls back if the account is not found or if the operation fails.
     *
     * @param id the UUID of the account to delete
     * @throws RuntimeException if the account is not found
     */
    @Transactional
    public void deleteAccount(UUID id) {
        Account accountEntity = accountRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Account not found with ID: " + id));
        accountRepository.delete(accountEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public TransferResponse transferMoney(TransactionDTO transactionDTO) {

        // Check if the source and target account IDs are the same
        if (transactionDTO.sourceAccountId().equals(transactionDTO.targetAccountId())) {
            throw new CustomBadRequestException("Source and target account IDs cannot be the same.");
        }

        Account sourceAccount = accountRepository.findById(transactionDTO.sourceAccountId())
                .orElseThrow(() -> new CustomNotFoundException("Source Account not found with ID: "));
        Account targetAccount = accountRepository.findById(transactionDTO.targetAccountId())
                .orElseThrow(() -> new CustomNotFoundException("Target Account not found with ID: "));

        if (sourceAccount.getBalance().compareTo(transactionDTO.amount()) < 0) {
            throw new CustomBadRequestException("Insufficient balance in the source account.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionDTO.amount()));
        targetAccount.setBalance(targetAccount.getBalance().add(transactionDTO.amount()));
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransferResponse(
                "Transfer successful",
                transactionDTO.amount() // Amount is the same as in the request
        );
    }
}